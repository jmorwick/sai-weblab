package net.sourcedestination.sai.weblab.controllers;

import net.sourcedestination.sai.reporting.Log;
import net.sourcedestination.sai.task.Task;
import net.sourcedestination.sai.weblab.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ReportsController {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private TaskManager taskManager;

    @GetMapping("/reports")
    public String view(Map<String, Object> model) {

        Map<String,String> logs = new HashMap<>();
        for(int tid : taskManager.getTrackedTaskIds()) {
            Task t = taskManager.getTask(tid);
            Log l = taskManager.getResult(tid);
            if(l != null) {
                logs.put(""+tid, l.getTaskName());
            }
        }
        model.put("logs", logs);
        return "reports";
    }

    @GetMapping("/reports/view/{taskid}")
    public String view(Map<String, Object> model,
                       @PathVariable("taskid") int taskid) {
        Log log = taskManager.getResult(taskid);
        model.put("taskid", ""+taskid);
        model.put("taskname", log.getTaskName());
        return "viewlog";
    }
}
