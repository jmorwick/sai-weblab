package net.sourcedestination.sai.weblab.controllers;

import net.sourcedestination.sai.weblab.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class TasksController {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private TaskManager taskManager;

    @GetMapping("tasks")
    public String view(Map<String, Object> model) {
        return "tasks";
    }
}
