package net.sourcedestination.sai.weblab.controllers;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.google.common.collect.Sets;
import net.sourcedestination.sai.reporting.logging.InteractiveAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class ReportsController {

    private static Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Autowired
    private ApplicationContext appContext;

    private final Map<Integer, Map<String, ? extends Object>> reportModels = new ConcurrentHashMap<>();
    private final Map<String, Supplier<Stream<String>>> logs = new ConcurrentHashMap<>();

    public Set<String> getLogNames() { return Sets.newHashSet(logs.keySet()); }

    public Stream<String> getLog(String name) { return logs.get(name).get(); }

    public int addReport(Map<String, ? extends Object> report) {
        synchronized (reportModels) {
            reportModels.put(reportModels.size()+1, report);
            return reportModels.size();
        }
    }

    @GetMapping("/reports")
    public String view(Map<String, Object> model) {
        model.put("reportids", reportModels.keySet());
        model.put("logids", logs.keySet());
        List<String> activeAppenders = new ArrayList<>();
        List<String> inactiveAppenders = new ArrayList<>();

        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        Iterator<Appender<ILoggingEvent>> i = context.getLogger("weblab-logger").iteratorForAppenders();

        for(InteractiveAppender a = (InteractiveAppender)i.next();
            a != null;
            a = i.hasNext() ? (InteractiveAppender)i.next() : null) {
            if(a.isListening())
                activeAppenders.add(a.getName());
            else
                inactiveAppenders.add(a.getName());

        }

        model.put("activeappenders", activeAppenders);
        model.put("inactiveappenders", inactiveAppenders);

        return "reports";
    }

    @PostMapping(value = "/reports/start-listening")
    public RedirectView startLogging(
            @RequestParam("appender") String appenderName) {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        InteractiveAppender l = (InteractiveAppender)context.getLogger("weblab-logger").getAppender(appenderName);
        if(!l.isListening()) {
            l.startListening();
        }
        return new RedirectView("/reports");
    }

    @PostMapping(value = "/reports/stop-listening")
    public RedirectView stopLogging(
            @RequestParam("appender") String appenderName) {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        InteractiveAppender l = (InteractiveAppender)context.getLogger("weblab-logger").getAppender(appenderName);
        if(l.isListening()) {
            logs.put((new Date().toString()), l.stopListening());
        }
        return new RedirectView("/reports");
    }

    @GetMapping("/reports/view/{reportid}")
    public String viewTask(Map<String, Object> model,
                       @PathVariable("reportid") int reportid) {
        model.put("reportid", ""+reportid);

        synchronized (reportModels) {
            model.putAll(reportModels.get(reportid));
        }

        // TODO: convert model to JSON and add JSON string to the model


        if(model.containsKey("view")) {
            return model.get("view").toString();
        }

        return "viewreport"; // TODO: determine how to handle views
    }



    @GetMapping("/reports/log/{logid}")
    public String viewTask(Map<String, Object> model,
                           @PathVariable("logid") String logId) {
        model.put("logid", ""+logId);

        synchronized (logs) {
            model.put("log",logs.get(logId).get().collect(Collectors.toList()));
        }

        if(model.containsKey("view")) {
            return model.get("view").toString();
        }

        return "viewlog";
    }


}
