package net.sourcedestination.sai.weblab.controllers;

import com.google.common.collect.Sets;
import net.sourcedestination.sai.analysis.ExperimentLogProcessor;
import net.sourcedestination.sai.analysis.ExperimentLogProcessorFactory;
import net.sourcedestination.sai.analysis.GraphProcessor;
import net.sourcedestination.sai.analysis.LogFileProcessor;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.GraphDeserializer;
import net.sourcedestination.sai.experiment.learning.ClassificationModelGenerator;
import net.sourcedestination.sai.db.DBPopulator;
import net.sourcedestination.sai.experiment.retrieval.QueryGenerator;
import net.sourcedestination.sai.experiment.retrieval.Retriever;
import net.sourcedestination.sai.util.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class TasksController {

    private static Logger logger = Logger.getLogger(TasksController.class.getCanonicalName());

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private ReportsController reportsController;

    @Autowired
    private DBInterfaceController dbInterfaceController;

    private int nextTaskId = 1;
    private Map<Integer,Task> trackedTasks = new HashMap<>();
    private Map<Integer,CompletableFuture> taskFutures = new HashMap<>();
    private Map<Integer,Date> startTimes = new HashMap<>();
    private Map<Integer,Date> endTimes = new HashMap<>();
    private Map<Integer, Long> taskTimes = new HashMap<>();

    public synchronized int addTask(Task t) {
        return addTask(t, x -> {});
    }

    public synchronized <T> int addTask(Task<T> t, Consumer<T> callback) {
        final int id = nextTaskId;
        final TasksController self = this;
        startTimes.put(id, new Date());
        trackedTasks.put(id, t);
        long startTime = System.nanoTime();
        CompletableFuture f = CompletableFuture.supplyAsync(t)
                .thenApply(result -> { // record the result here when finished
                    synchronized(self) {
                        endTimes.put(id, new Date());
                        taskTimes.put(id, (System.nanoTime() - startTime) / 1000000);
                    }
                    return result;
                }).thenAccept(callback)
                .exceptionally(e -> {
                    t.cancel();
                    logger.throwing(getClass().getCanonicalName(), "Error executing task: ", e);
                    return null;
                });
        taskFutures.put(nextTaskId, f);
        return nextTaskId++;
    }

    public synchronized Date getStartTime(int id) { return startTimes.get(id); }

    public synchronized Date getEndTime(int id) { return endTimes.get(id); }

    public synchronized Object getResult(int id) {
        try {
            return taskFutures.get(id).get();
        } catch(Exception e) {
            return null;
        }
    }

    public synchronized Long getTaskTime(int id) { return taskTimes.get(id); }

    public synchronized Task getTask(int id) {
        return trackedTasks.get(id);
    }

    public synchronized Set<Integer> getTrackedTaskIds() {
        return new HashSet<Integer>(trackedTasks.keySet());
    }

    public synchronized Set<Integer> getCompletedTaskIds() {
        return new HashSet<Integer>(endTimes.keySet());
    }

    public synchronized Set<Integer> getRunningTaskIds() {
        return Sets.difference(getTrackedTaskIds(), getCompletedTaskIds());
    }
    @GetMapping("/tasks")
    public String view(Map<String, Object> model) {
        Map<String,String> activeTasks = new HashMap<>();
        Map<String,String> inactiveTasks = new HashMap<>();
        Map<String,Double> percentageComplete = new HashMap<>();
        Map<String,Integer> progress = new HashMap<>();
        Map<String,String> startTimes = new HashMap<>();
        Map<String,String> endTimes = new HashMap<>();
        Map<String, String> taskTimes = new HashMap<>();
        Map<String,String> results = new HashMap<>();

        // populate info for currently running tasks
        for(int tid : getRunningTaskIds()) {
            Task t = getTask(tid);
            percentageComplete.put(""+tid, t.getPercentageDone());
            progress.put(""+tid, t.getProgressUnits());
            activeTasks.put(""+tid, ""+t.getTaskName());
            startTimes.put(""+tid, getStartTime(tid).toString());
        }

        // populate info for completed tasks
        for(int tid : getCompletedTaskIds()) {
            Task t = getTask(tid);
            inactiveTasks.put(""+tid, ""+t.getTaskName());
            startTimes.put(""+tid, getStartTime(tid).toString());
            endTimes.put(""+tid, getEndTime(tid).toString());
            taskTimes.put(""+tid, ""+(getEndTime(tid).getTime() - getStartTime(tid).getTime()));
            Object result = getResult(tid);
            results.put(""+tid, result == null ? "n/a" : result.toString());
        }

        // find db interfaces
        Map<String, DBInterface> dbs = appContext.getBeansOfType(DBInterface.class);
        Map<String, String> dbsinfo = dbs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));

        model.put("dbs", dbsinfo);

        // find decoders
        Map<String, GraphDeserializer> decoders = appContext.getBeansOfType(GraphDeserializer.class);
        Set<String> decoderNames = decoders.keySet();
        model.put("decoders", decoderNames);
        model.put("defaultdecoder", dbInterfaceController.getSession().getAttribute("defaultdecoder"));

        // find populators
        Map<String, DBPopulator> populators = appContext.getBeansOfType(DBPopulator.class);
        model.put("populators", populators.keySet());

        // find retrievers
        Map<String, Retriever> retrievers = appContext.getBeansOfType(Retriever.class);
        model.put("retrievers", retrievers.keySet());

        // find query generators
        Map<String, QueryGenerator> generators = appContext.getBeansOfType(QueryGenerator.class);
        model.put("generators", generators.keySet());

        // find log processors
        Map<String, ExperimentLogProcessorFactory> logProcessors =
                appContext.getBeansOfType(ExperimentLogProcessorFactory.class);
        Map<String, String> logProcessorInfo = logProcessors.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));

        model.put("logprocessors", logProcessorInfo);

        Map<String, ClassificationModelGenerator> classifiers =
                appContext.getBeansOfType(ClassificationModelGenerator.class);

        model.put("dbs", dbsinfo);
        model.put("classifiers", classifiers.keySet());
        model.put("activetasks", activeTasks);
        model.put("inactivetasks", inactiveTasks);
        model.put("startTimes", startTimes);
        model.put("endTimes", endTimes);
        model.put("taskTimes", taskTimes);
        model.put("percentagecomplete", percentageComplete);
        model.put("progress", progress);
        return "tasks";
    }

    @PostMapping(value = "/tasks/retrieval")
    public RedirectView simpleRetrieval(@RequestParam("retriever") String retrieverName,
                                        @RequestParam("query") String queryString,
                                        @RequestParam(name="generator", required = false) String generator,
                                        @RequestParam(name="format", required = false) String format,
                                        @RequestParam(name="skip",required = false) Long skipResults,
                                        @RequestParam(name="max",required = false) Long maxResults,
                                        @RequestParam(name = "processors[]", required = false)
                                                    String[] graphProcessingBeanNames) {
        Retriever retriever = (Retriever) appContext.getBean(retrieverName);

        // find graph processing beans
        final GraphProcessor[] graphProcessingBeans;
        if(graphProcessingBeanNames != null) {
            graphProcessingBeans = new GraphProcessor[graphProcessingBeanNames.length];
            for(int i=0; i<graphProcessingBeanNames.length; i++)
                graphProcessingBeans[i] = ((GraphProcessor) appContext.getBean(graphProcessingBeanNames[i]));

        } else {
            graphProcessingBeans = new GraphProcessor[0];
        }


        final QueryGenerator gen;
        if(generator != null && generator.length() > 0 &&
                  !generator.equals("manual") && !generator.equals("none")){
            logger.info("using query generator " + generator + " with retriever " + retrieverName);
            gen = (QueryGenerator)appContext.getBean(generator);
        } else if (generator.equals("manual")) {
            logger.info("decoding graph query w/ hash " + queryString.hashCode() + " using decoder " + format
             + " for retriever " + retrieverName);
            // a format and query were specified
            dbInterfaceController.getSession().setAttribute("defaultdecoder", format);  // remember this choice
            final GraphDeserializer deserializer =
                    (GraphDeserializer) appContext.getBean(format);
            gen = QueryGenerator.of(deserializer.apply(queryString));
        } else { // no query used -- retrieval results are not dependent on the query
            logger.info("issuing meaningless query to retriever " + retrieverName );
            gen = (QueryGenerator) () -> Stream.of("unused query");
        }

        if(skipResults != null)
            retriever = Retriever.skipResults(retriever, skipResults);
        if(maxResults != null)
            retriever = Retriever.limitResults(retriever, maxResults);

        addTask(Retriever.retrievalExperiment(retriever, gen));

        return new RedirectView("/tasks");
    }

    @PostMapping(value = "/tasks/populate")
    public RedirectView populateDB(@RequestParam("dbname") String dbname,
                                   @RequestParam("populatorname") String populatorname) {
        final DBInterface db = (DBInterface) appContext.getBean(dbname);
        final DBPopulator pop = (DBPopulator) appContext.getBean(populatorname);
        int taskId = addTask(pop.apply(db));
        return new RedirectView("/tasks");
    }

    @PostMapping("/tasks/process-log-file")
    public RedirectView loadReport(@RequestParam("file") MultipartFile file,
                                   @RequestParam(name = "processors[]", required = false)
                                           String[] logProcessingBeanNames)
            throws IOException {

        // find processing beans
        final ExperimentLogProcessor[] logProcessingBeans;
        if(logProcessingBeanNames != null) {
            logProcessingBeans = new ExperimentLogProcessor[logProcessingBeanNames.length];
            for(int i=0; i<logProcessingBeanNames.length; i++)
                logProcessingBeans[i] =
                        ((ExperimentLogProcessorFactory)
                          appContext.getBean(logProcessingBeanNames[i])
                        ).get();

        } else {
            logProcessingBeans = new ExperimentLogProcessor[0];
        }

        LogFileProcessor task = new LogFileProcessor(file.getInputStream(), file.getSize(), logProcessingBeans);
        addTask(task, report -> {
            reportsController.addReport(report);
        });
        return new RedirectView("/tasks");
    }
}