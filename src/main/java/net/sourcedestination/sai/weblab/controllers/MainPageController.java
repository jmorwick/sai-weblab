package net.sourcedestination.sai.weblab.controllers;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourcedestination.sai.db.DBInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @Autowired
    private ApplicationContext appContext;

    @GetMapping({"/","dbs"})
    public String view(Map<String, Object> model) {
        Map<String,DBInterface> dbs = appContext.getBeansOfType(DBInterface.class);
        Map<String,String> dbsinfo = dbs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));
        model.put("dbs", dbsinfo);
        return "main";
    }

}