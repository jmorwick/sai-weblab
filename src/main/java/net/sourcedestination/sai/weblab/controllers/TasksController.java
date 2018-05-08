package net.sourcedestination.sai.weblab.controllers;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.learning.ClassificationModelGenerator;
import net.sourcedestination.sai.reporting.Log;
import net.sourcedestination.sai.task.Task;
import net.sourcedestination.sai.weblab.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        for(int tid : taskManager.getTrackedTaskIds()) {
            Task t = taskManager.getTask(tid);
            percentageComplete.put(""+tid, t.getPercentageDone());
            progress.put(""+tid, t.getProgressUnits());
            activeTasks.put(""+tid, ""+t.getTaskName());
            startTimes.put(""+tid, taskManager.getStartTime(tid).toString());
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
}
