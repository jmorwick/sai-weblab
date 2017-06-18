package net.sourcedestination.sai.weblab.controllers;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourcedestination.sai.db.DBInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DBInterfaceController {

    private final ApplicationContext appContext;

    @Autowired
    public DBInterfaceController(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @GetMapping({"/","/dbs"})
    public String view(Map<String, Object> model) {
        Map<String,DBInterface> dbs = appContext.getBeansOfType(DBInterface.class);
        Map<String,String> dbsinfo = dbs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));

        model.put("dbs", dbsinfo);
        return "main";
    }

    @GetMapping({"/dbs/view/{dbname}"})
    public String viewDB(Map<String, Object> model,
                         @PathVariable("dbname") String dbname) {
        DBInterface db = (DBInterface)appContext.getBean(dbname);
        Map<String,String> stats = new HashMap<>(); // TODO: retrieve from appContext (need new class in SAI)
        model.put("dbname", dbname);
        model.put("stats", stats);
        return "viewdb";
    }

    @GetMapping({"/dbs/retrieve/{dbname}"})
    public String viewGraph(Map<String, Object> model,
                         @PathVariable("dbname") String dbname,
                         @RequestParam("id") int id) {
        DBInterface db = (DBInterface)appContext.getBean(dbname);
        model.put("dbname", dbname);
        return "viewdb";
    }

}