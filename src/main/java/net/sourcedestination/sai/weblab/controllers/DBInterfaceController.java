package net.sourcedestination.sai.weblab.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import net.sourcedestination.funcles.tuple.Tuple2;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphDeserializer;
import net.sourcedestination.sai.graph.GraphSerializer;
import net.sourcedestination.sai.reporting.metrics.DBMetric;
import net.sourcedestination.sai.reporting.metrics.FastDBMetric;
import net.sourcedestination.sai.retrieval.GraphRetriever;
import net.sourcedestination.sai.task.DBPopulator;
import net.sourcedestination.sai.task.Task;
import net.sourcedestination.sai.weblab.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;

import static net.sourcedestination.funcles.tuple.Tuple2.makeTuple;

@Controller
public class DBInterfaceController {

    private static Logger logger = Logger.getLogger(DBInterfaceController.class);

    private final ApplicationContext appContext;
    private final Map<Tuple2<DBInterface, DBMetric>, Task> statTasks;
    private final Map<Tuple2<DBInterface, DBMetric>, Double> statValues;
    private TaskManager taskManager;


    public static HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        if(session.getAttribute("defaultencoder") == null)
            session.setAttribute("defaultencoder", "none");
        if(session.getAttribute("defaultdecoder") == null)
            session.setAttribute("defaultdecoder", "none");
        return session;
    }

    @Autowired
    public DBInterfaceController(ApplicationContext appContext,
                                 TaskManager taskManager) {
        this.statTasks = new ConcurrentHashMap<>();
        this.statValues = new ConcurrentHashMap<>();
        this.taskManager = taskManager;
        this.appContext = appContext;
    }

    @GetMapping({"/", "/dbs"})
    public String view(Map<String, Object> model) {
        Map<String, DBInterface> dbs = appContext.getBeansOfType(DBInterface.class);
        Map<String, String> dbsinfo = dbs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getClass().getSimpleName()));

        model.put("dbs", dbsinfo);
        model.put("defaultencoder", getSession().getAttribute("defaultencoder"));
        model.put("defaultdecoder", getSession().getAttribute("defaultdecoder"));
        return "main";
    }

    @GetMapping({"/dbs/view/{dbname}"})
    public String viewDB(Map<String, Object> model,
                         @PathVariable("dbname") String dbname) {
        DBInterface db = (DBInterface) appContext.getBean(dbname);
        model.put("dbname", dbname);

        // check stats
        Map<String, String> stats = new HashMap<>();
        Map<String, String> statProgress = new HashMap<>();
        Map<String, String> statComputable = new HashMap<>();
        Map<String, DBMetric> statGens = appContext.getBeansOfType(DBMetric.class);
        for (String statName : statGens.keySet()) {
            DBMetric stat = statGens.get(statName);
            if (stat instanceof FastDBMetric) {
                stats.put(statName, "" + stat.apply(db).get());
            } else if (statValues.containsKey(makeTuple(db, stat))) {
                statComputable.put(statName, statName);
                stats.put(statName, statValues.get(makeTuple(db, stat)).toString());
            } else {
                statComputable.put(statName, statName);
                stats.put(statName, "");
            }

            if (statTasks.containsKey(makeTuple(db, stat))) {
                statProgress.put(statName, "" + (
                        100.0 * statTasks.get(makeTuple(db, stat)).getPercentageDone()));
            }
        }

        model.put("stats", stats);
        model.put("statProgress", statProgress);
        model.put("statComputable", statComputable);

        // find encoders
        Map<String, GraphSerializer> encoders = appContext.getBeansOfType(GraphSerializer.class);
        model.put("encoders", encoders.keySet());

        // find decoders
        Map<String, GraphDeserializer> decoders = appContext.getBeansOfType(GraphDeserializer.class);
        Set<String> decoderNames = decoders.keySet();
        model.put("decoders", decoderNames);

        // find populators
        Map<String, DBPopulator> populators = appContext.getBeansOfType(DBPopulator.class);
        model.put("populators", populators.keySet());


        // find retrievers
        Map<String, GraphRetriever> retrievers = appContext.getBeansOfType(GraphRetriever.class);
        model.put("retrievers", retrievers.keySet());


        model.put("defaultencoder", getSession().getAttribute("defaultencoder"));
        model.put("defaultdecoder", getSession().getAttribute("defaultdecoder"));
        return "viewdb";
    }

    @PostMapping(value = "/dbs/recompute/{dbname}/{statname}")
    public RedirectView recomputeStat(@PathVariable("dbname") String dbname,
                                      @PathVariable("statname") String statname) {
        final DBInterface db = (DBInterface) appContext.getBean(dbname);
        final DBMetric stat = (DBMetric) appContext.getBean(statname);
        final Tuple2<DBInterface, DBMetric> key = makeTuple(db, stat);
        if (!statTasks.containsKey(key)) {
            Task<Double> t = stat.apply(db);
            statTasks.put(key, t);
            CompletableFuture.supplyAsync(t)
                    .thenAccept(value -> {
                        statValues.put(key, value);
                        statTasks.remove(key);
                    });
        }

        return new RedirectView("/dbs/view/" + dbname);
    }

    @PostMapping(value = "/dbs/create/{dbname}")
    public RedirectView createGraph(@PathVariable("dbname") String dbname,
                                    @RequestParam("format") String format,
                                    @RequestParam("encoding") String encoding) {
        final DBInterface db = (DBInterface) appContext.getBean(dbname);
        final GraphDeserializer<? extends Graph> encoder =
                (GraphDeserializer) appContext.getBean(format);
        final Graph g = encoder.apply(encoding);
        final int id = db.addGraph(g);

        return new RedirectView("/dbs/view/" + dbname);
    }

    @GetMapping({"/dbs/retrieve/{dbname}"})
    public String viewGraph(Map<String, Object> model,
                            @PathVariable("dbname") String dbname,
                            @RequestParam("id") int id,
                            @RequestParam("format") String format) {
        logger.info("User initiated retrieval of graph #"+id +
            " to " + format + " from " + dbname);

        DBInterface db = (DBInterface) appContext.getBean(dbname);
        Graph g = db.retrieveGraph(id);
        Map<String, GraphSerializer> encoders = appContext.getBeansOfType(GraphSerializer.class);
        Set<String> encoderNames = encoders.keySet();

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
        return new RedirectView("/dbs/view/" + dbname);
    }


    @PostMapping(value = "/dbs/populate/{dbname}")
    public RedirectView populateDB(@PathVariable("dbname") String dbname,
                                   @RequestParam("populatorname") String populatorname) {
        final DBInterface db = (DBInterface) appContext.getBean(dbname);
        final DBPopulator pop = (DBPopulator) appContext.getBean(populatorname);
        int taskId = taskManager.addTask(pop.apply(db));
        return new RedirectView("/tasks");

    }

}