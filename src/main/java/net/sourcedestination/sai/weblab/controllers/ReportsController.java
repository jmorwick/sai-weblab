package net.sourcedestination.sai.weblab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class ReportsController {

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("reports")
    public String view(Map<String, Object> model) {
        return "reports";
    }
}
