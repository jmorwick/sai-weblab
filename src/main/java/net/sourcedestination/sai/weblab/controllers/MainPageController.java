package net.sourcedestination.sai.weblab.controllers;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {
    @GetMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("dbs", null);  // TODO: send list of database interfaces
        return "main";
    }

}