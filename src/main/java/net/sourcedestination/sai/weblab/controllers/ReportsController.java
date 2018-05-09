package net.sourcedestination.sai.weblab.controllers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.task.Task;
import net.sourcedestination.sai.weblab.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        }
        model.put("logs", logs);
        return "reports";
    }

    @GetMapping("/reports/view/{taskid}")
    public String viewTask(Map<String, Object> model,
                       @PathVariable("taskid") int taskid) {
        model.put("taskid", ""+taskid);
        return "viewlog";
    }

    @GetMapping("/reports/view-query/{taskid}/{queryid}")
    public String viewQuery(Map<String, Object> model,
                            @PathVariable("taskid") int taskid,
                            @PathVariable("queryid") int queryid) {

        BiMap<String,DBInterface> dbs =
                HashBiMap.create(appContext.getBeansOfType(DBInterface.class));

        model.put("taskid", ""+taskid);
        model.put("queryid", ""+queryid);
        return "viewquery";
    }


}
