package net.sourcedestination.sai.weblab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ReportsController {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private TasksController tasksController;

    private Map<String, Map<String, Object>> reportModels
            = new ConcurrentHashMap<>();

    @GetMapping("/reports")
    public String view(Map<String, Object> model) {
        model.put("reports", reportModels.keySet());


        return "reports";
    }

    @GetMapping("/reports/view/{reportid}")
    public String viewTask(Map<String, Object> model,
                       @PathVariable("reportid") int reportid) {
        model.put("reportid", ""+reportid);
        model.putAll(reportModels.get(reportid));
        return "viewlog"; // TODO: determine how to handle views
    }

    @PostMapping("/reports/load/{reportid}")
    public RedirectView loadReport(@PathVariable("reportid") String reportid) {

        // TODO: load JSON data from post

        return new RedirectView("/reports");
    }


}
