package net.sourcedestination.sai.weblab.controllers;

import net.sourcedestination.sai.reporting.Log;
import net.sourcedestination.sai.task.Task;
import net.sourcedestination.sai.weblab.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

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
        for(int tid : taskManager.getTrackedTaskIds()) {
            Task t = taskManager.getTask(tid);
            Log l = taskManager.getResult(tid);
            if(l != null) inactiveTasks.put(""+tid, ""+t.getTaskName());
            else {
                percentageComplete.put(""+tid, t.getPercentageDone());
                progress.put(""+tid, t.getProgressUnits());
                activeTasks.put(""+tid, ""+t.getTaskName());
            }
        }
        model.put("activetasks", activeTasks);
        model.put("inactivetasks", inactiveTasks);
        model.put("percentagecomplete", percentageComplete);
        model.put("progress", progress);
        return "tasks";
    }
}
