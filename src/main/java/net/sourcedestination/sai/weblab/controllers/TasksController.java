package net.sourcedestination.sai.weblab.controllers;

import com.google.common.collect.Sets;
import net.sourcedestination.sai.analysis.GraphProcessor;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphDeserializer;
import net.sourcedestination.sai.learning.ClassificationModelGenerator;
import net.sourcedestination.sai.retrieval.GraphRetriever;
import net.sourcedestination.sai.task.Task;
import net.sourcedestination.sai.weblab.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.sourcedestination.sai.weblab.controllers.DBInterfaceController.getSession;

@Controller
public class TasksController {

    @Autowired
    private ApplicationContext appContext;


    private int nextTaskId = 1;
    private Map<Integer,Task> trackedTasks = new HashMap<>();
    private Map<Integer,CompletableFuture> taskFutures = new HashMap<>();
    private Map<Integer,Date> startTimes = new HashMap<>();
    private Map<Integer,Date> endTimes = new HashMap<>();
    private Map<Integer, Long> taskTimes = new HashMap<>();

    public synchronized int addTask(Task t) {
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
    @GetMapping("tasks")
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


        Map<String, DBInterface> dbs = appContext.getBeansOfType(DBInterface.class);
        Map<String, String> dbsinfo = dbs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));

        model.put("dbs", dbsinfo);


        Map<String, GraphProcessor> processors = appContext.getBeansOfType(GraphProcessor.class);
        Map<String, String> processorinfo = processors.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));

        model.put("processors", processorinfo);

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
    public RedirectView simpleRetrieval(@RequestParam("dbname") String dbname,
                                        @RequestParam("retriever") String retrieverName,
                                        @RequestParam("query") String queryString,
                                        @RequestParam("format") String format,
                                        @RequestParam("skip") int skipResults,
                                        @RequestParam("max") int maxResults,
                                        @RequestParam(name = "processors[]", required = false)
                                                    String[] graphProcessingBeanNames) {
        final DBInterface db = (DBInterface) appContext.getBean(dbname);
        final GraphRetriever retriever = (GraphRetriever) appContext.getBean(retrieverName);

        // find graph processing beans
        final GraphProcessor[] graphProcessingBeans;
        if(graphProcessingBeanNames != null) {
            graphProcessingBeans = new GraphProcessor[graphProcessingBeanNames.length];
            for(int i=0; i<graphProcessingBeanNames.length; i++)
                graphProcessingBeans[i] = ((GraphProcessor) appContext.getBean(graphProcessingBeanNames[i]));

        } else {
            graphProcessingBeans = new GraphProcessor[0];
        }


        Graph query;
        if (format != null && format.length() > 0 && !format.equals("none") &&
                queryString != null && queryString.length() > 0) {
            // a format and query were specified
            getSession().setAttribute("defaultdecoder", format);  // remember this choice
            final GraphDeserializer<? extends Graph> deserializer = (GraphDeserializer<? extends Graph>) appContext.getBean(format);
            // TODO: compiler won't accept GraphDeserializer here w/o type arg... not immediately sure why, but need to fix def of GraphSerializer class to fix this
            query = deserializer.apply(queryString);
        } else {
            query = null;
        }

        addTask(new Task() {
            private AtomicInteger progress = new AtomicInteger(0);
            public Object get() {
                retriever.retrieve(db,query)
                        .skip(skipResults)
                        .limit(maxResults)
                        .forEach(gid -> {
                            progress.incrementAndGet();
                            GraphRetriever.logger.info("retrieved Graph ID #"+gid+" from "+dbname);
                        });
                progress.set(maxResults);
                return null;
            }

            @Override
            public String getTaskName() { return retriever.getClass().getName(); }

            @Override
            public int getProgressUnits() { return progress.get(); }

            @Override
            public int getTotalProgressUnits() { return maxResults; }

        });

        return new RedirectView("/tasks");
    }
}