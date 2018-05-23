package net.sourcedestination.sai.weblab.controllers;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.graph.GraphDeserializer;
import net.sourcedestination.sai.db.graph.GraphSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.RedirectView;


import javax.servlet.http.HttpSession;

@Controller
public class DBInterfaceController {

    @Autowired
    private ApplicationContext appContext;


    public HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        if(session.getAttribute("defaultencoder") == null) {
            Map<String, GraphSerializer> encoders = appContext.getBeansOfType(GraphSerializer.class);
            session.setAttribute("defaultencoder", encoders.keySet().stream().findFirst().orElse("none"));
        }
        if(session.getAttribute("defaultdecoder") == null){
            Map<String, GraphDeserializer> decoders = appContext.getBeansOfType(GraphDeserializer.class);
            session.setAttribute("defaultdecoder", decoders.keySet().stream().findFirst().orElse("none"));
        }
        if(session.getAttribute("defaultdb") == null) {
            Map<String, DBInterface> dbs = appContext.getBeansOfType(DBInterface.class);
            session.setAttribute("defaultdb", dbs.keySet().stream().findFirst().orElse("none"));
        }
        return session;
    }

    @GetMapping({"/dbs", "/"})
    public String viewDB(Map<String, Object> model,
                         @RequestParam(value = "selecteddb", required = false) String selecteddb) {

        Map<String, DBInterface> dbs = appContext.getBeansOfType(DBInterface.class);
        Map<String, String> dbsinfo = dbs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));

        if(selecteddb == null) {
            selecteddb = (String)getSession().getAttribute("defaultdb");
        }
        model.put("dbs", dbsinfo);
        model.put("selecteddb", selecteddb);

        if(!selecteddb.equals("none")) {
            DBInterface db = (DBInterface) appContext.getBean(selecteddb);

            getSession().setAttribute("defaultdb", selecteddb);

            model.put("dbsize", db.getDatabaseSize());

            // find encoders
            Map<String, GraphSerializer> encoders = appContext.getBeansOfType(GraphSerializer.class);
            model.put("encoders", encoders.keySet());

            // find decoders
            Map<String, GraphDeserializer> decoders = appContext.getBeansOfType(GraphDeserializer.class);
            Set<String> decoderNames = decoders.keySet();
            model.put("decoders", decoderNames);
            model.put("defaultdecoder", getSession().getAttribute("defaultdecoder"));
        }
        return "viewdb";
    }

    @PostMapping(value = "/dbs/create/{dbname}")
    public RedirectView createGraph(@PathVariable("dbname") String dbname,
                                    @RequestParam("format") String format,
                                    @RequestParam("encoding") String encoding) {
        final DBInterface db = (DBInterface) appContext.getBean(dbname);
        final GraphDeserializer encoder =
                (GraphDeserializer) appContext.getBean(format);
        final Graph g = encoder.apply(encoding);
        final int id = db.addGraph(g);

        return new RedirectView("/dbs");
    }

    @GetMapping({"/dbs/retrieve/{dbname}/{id}"})
    public String viewGraph(Map<String, Object> model,
                            @PathVariable("dbname") String dbname,
                            @PathVariable("id") int id,
                            @RequestParam(name="format",required=false) String format) {

        DBInterface db = (DBInterface) appContext.getBean(dbname);
        Graph g = db.retrieveGraph(id);
        Map<String, GraphSerializer> encoders = appContext.getBeansOfType(GraphSerializer.class);
        Set<String> encoderNames = encoders.keySet();
        if(format == null)
            format = getSession().getAttribute("defaultencoder").toString();
        model.put("id", id);
        model.put("encoders", encoderNames);
        model.put("encoder", format);
        getSession().setAttribute("defaultencoder", format); // remember the choice
        model.put("encoding", encoders.get(format).apply(g));
        model.put("dbname", dbname);
        model.put("defaultencoder", getSession().getAttribute("defaultencoder"));
        model.put("defaultdecoder", getSession().getAttribute("defaultdecoder"));
        return "viewgraph";
    }


    @GetMapping({"/dbs/export/{dbname}"})
    public String exportDatabase(Map<String, Object> model,
                            @PathVariable("dbname") String dbname,
                            @RequestParam("format") String format) {
        DBInterface db = (DBInterface) appContext.getBean(dbname);
        Map<String, GraphSerializer> encoders = appContext.getBeansOfType(GraphSerializer.class);
        Set<String> encoderNames = encoders.keySet();
        model.put("encoders", encoderNames);
        model.put("encoder", format);
        getSession().setAttribute("defaultencoder", format); // remember the choice
        model.put("encoding",
                db.getGraphIDStream()
                .map(gid -> db.retrieveGraph(gid))
                .map(graph -> encoders.get(format).apply(graph))
                .reduce((x,y) -> x+"\n"+y)
                .get()
        );
        model.put("dbname", dbname);
        model.put("defaultencoder", getSession().getAttribute("defaultencoder"));
        model.put("defaultdecoder", getSession().getAttribute("defaultdecoder"));
        return "exportdb";
    }


    @PostMapping(value = "/dbs/clear/{dbname}")
    public RedirectView clearDB(@PathVariable("dbname") String dbname) {
        final DBInterface db = (DBInterface) appContext.getBean(dbname);
        db.getGraphIDStream().forEach(db::deleteGraph);
        return new RedirectView("/dbs");
    }

}