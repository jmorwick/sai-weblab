package net.sourcedestination.sai.weblab.controllers;

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

    private final Map<Integer, Map<String, ? extends Object>> reportModels = new HashMap<>();

    public int addReport(Map<String, ? extends Object> report) {

        // TODO: clean up any JSON objects in the report

        synchronized (reportModels) {
            reportModels.put(reportModels.size()+1, report);
            return reportModels.size();
        }
    }

    @GetMapping("/reports")
    public String view(Map<String, Object> model) {
        synchronized (reportModels) {
            model.put("reportids", reportModels.keySet());
        }

        return "reports";
    }

    @GetMapping("/reports/view/{reportid}")
    public String viewTask(Map<String, Object> model,
                       @PathVariable("reportid") int reportid) {
        model.put("reportid", ""+reportid);
        synchronized (reportModels) {
            model.putAll(reportModels.get(reportid));
        }
        return "viewreport"; // TODO: determine how to handle views
    }

}
