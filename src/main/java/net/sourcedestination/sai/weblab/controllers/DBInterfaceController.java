package net.sourcedestination.sai.weblab.controllers;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourcedestination.sai.db.DBInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DBInterfaceController {

    @Autowired
    private ApplicationContext appContext;

    private Map<String,DBInterface> runtimeDbs = new HashMap<>();

    @GetMapping({"/","/dbs"})
    public String view(Map<String, Object> model) {
        Map<String,DBInterface> dbs = appContext.getBeansOfType(DBInterface.class);
        Map<String,String> dbsinfo = dbs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));
        runtimeDbs.entrySet().stream()
                .forEach(e -> dbsinfo.put(
                        e.getKey(),
                        e.getValue().getClass().getSimpleName()));

        model.put("dbs", dbsinfo);
        return "main";
    }

    @GetMapping({"/dbs/view/{dbname}"})
    public String viewDB(Map<String, Object> model,
                         @PathVariable("dbname") String dbname) {
        DBInterface db = runtimeDbs.get(dbname);
        if(db == null) db = (DBInterface)appContext.getBean(dbname);
        model.put("dbname", dbname);
        return "viewdb";
    }

}