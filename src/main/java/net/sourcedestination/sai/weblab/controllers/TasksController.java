package net.sourcedestination.sai.weblab.controllers;

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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.sourcedestination.sai.weblab.controllers.DBInterfaceController.getSession;

@Controller
public class TasksController {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private TaskManager taskManager;

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
        for(int tid : taskManager.getRunningTaskIds()) {
            Task t = taskManager.getTask(tid);
            percentageComplete.put(""+tid, t.getPercentageDone());
            progress.put(""+tid, t.getProgressUnits());
            activeTasks.put(""+tid, ""+t.getTaskName());
            startTimes.put(""+tid, taskManager.getStartTime(tid).toString());
        }

        // populate info for completed tasks
        for(int tid : taskManager.getCompletedTaskIds()) {
            Task t = taskManager.getTask(tid);
            inactiveTasks.put(""+tid, ""+t.getTaskName());
            startTimes.put(""+tid, taskManager.getStartTime(tid).toString());
            endTimes.put(""+tid, taskManager.getEndTime(tid).toString());
            Object result = taskManager.getResult(tid);
            results.put(""+tid, result == null ? "n/a" : result.toString());
        }


        Map<String, DBInterface> dbs = appContext.getBeansOfType(DBInterface.class);
        Map<String, String> dbsinfo = dbs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));

        model.put("dbs", dbsinfo);


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
                                        @RequestParam("max") int maxResults) {
        final DBInterface db = (DBInterface) appContext.getBean(dbname);
        final GraphRetriever retriever = (GraphRetriever) appContext.getBean(retrieverName);
        Graph query;
        if (format != null && format.length() > 0 && !format.equals("none") &&
                queryString != null && queryString.length() > 0) {
            // a format and query were specified
            getSession().setAttribute("defaultdecoder", format);  // remember this choice
            final GraphDeserializer<? extends Graph> deserializer = (GraphDeserializer<? extends Graph>) appContext.getBean(format);
            // TODO: compiler won't accept GraphDeserializer here w/o type arg... not immediately sure why, but need to fix def of GraphSerializer class to fix this
            query = deserializer.apply(queryString);
        } else {
            throw new RuntimeException("bad query"); // TODO: clean this up
        }
        GraphRetriever wrappedRetriever = new GraphRetriever() {
            @Override
            public Stream<Integer> retrieve(DBInterface db, Graph graph) {
                return retriever.retrieve(db, graph).skip(skipResults).limit(maxResults);
            }
        };

        taskManager.addTask(new Task() {
            private AtomicInteger progress = new AtomicInteger(0);
            public Object get() {
                retriever.retrieve(db,query).forEach(
                        gid -> {
                            progress.incrementAndGet();
                        }
                    );
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