package net.sourcedestination.sai.weblab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ReportsController {

    @Autowired
    private ApplicationContext appContext;

    private Map<Integer, Map<String, Object>> reportModels
            = new ConcurrentHashMap<>();

    public void addReport(Integer id, Map<String, Object> report) {
        reportModels.put(id, report);
    }

    @GetMapping("/reports")
    public String view(Map<String, Object> model) {
        model.put("reportids", reportModels.keySet());


        return "reports";
    }

    @GetMapping("/reports/view/{reportid}")
    public String viewTask(Map<String, Object> model,
                       @PathVariable("reportid") int reportid) {
        model.put("reportid", ""+reportid);
        model.putAll(reportModels.get(reportid));
        return "viewlog"; // TODO: determine how to handle views
    }

}
