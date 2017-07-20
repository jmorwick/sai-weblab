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
import net.sourcedestination.sai.graph.MutableGraph;
import net.sourcedestination.sai.reporting.stats.DBStatistic;
import net.sourcedestination.sai.task.DBPopulator;
import net.sourcedestination.sai.task.Task;
import net.sourcedestination.sai.weblab.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import static net.sourcedestination.funcles.tuple.Tuple2.makeTuple;

@Controller
public class DBInterfaceController {

    private final ApplicationContext appContext;
    private final Map<Tuple2<DBInterface,DBStatistic>,Task> statTasks;
    private final Map<Tuple2<DBInterface,DBStatistic>,Double> statValues;
    private TaskManager taskManager;

    @Autowired
    public DBInterfaceController(ApplicationContext appContext,
                                 TaskManager taskManager) {
        this.statTasks = new ConcurrentHashMap<>();
        this.statValues = new ConcurrentHashMap<>();
        this.taskManager = taskManager;
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
        model.put("dbname", dbname);

        // check stats
        Map<String,String> stats = new HashMap<>();
        Map<String,String> statProgress = new HashMap<>();
        Map<String,DBStatistic> statGens = appContext.getBeansOfType(DBStatistic.class);
        for(String statName : statGens.keySet()) {
            DBStatistic stat = statGens.get(statName);
            if(statValues.containsKey(makeTuple(db,stat))) {
                stats.put(statName, statValues.get(makeTuple(db,stat)).toString());
            } else {
                stats.put(statName, "");
            }

            if(statTasks.containsKey(makeTuple(db,stat))) {
                stats.put(statName, ""+statTasks.get(makeTuple(db,stat)).getPercentageDone());
            }
        }
        model.put("stats", stats);
        model.put("statProgress", statProgress);

        // find encoders
        Map<String,GraphDeserializer> encoders = appContext.getBeansOfType(GraphDeserializer.class);
        model.put("decoders", encoders.keySet());

        // find populators
        Map<String,DBPopulator> populators = appContext.getBeansOfType(DBPopulator.class);
        model.put("populators", populators.keySet());



        return "viewdb";
    }

    @PostMapping(value="/dbs/recompute/{dbname}/{statname}")
    public RedirectView recomputeStat(@PathVariable("dbname") String dbname,
                                      @PathVariable("statname") String statname) {
        final DBInterface db = (DBInterface)appContext.getBean(dbname);
        final DBStatistic stat = (DBStatistic) appContext.getBean(statname);
        final Tuple2<DBInterface, DBStatistic> key = makeTuple(db,stat);
        if(!statTasks.containsKey(key)) {
            Task<Double> t = stat.apply(db);
            CompletableFuture.supplyAsync(t)
                    .thenAccept(value -> {
                        statValues.put(key, value);
                        statTasks.remove(key);
                    });
        }

        return new RedirectView("/dbs/view/"+dbname);
    }

    @PostMapping(value="/dbs/create/{dbname}")
    public RedirectView createGraph(@PathVariable("dbname") String dbname,
                                    @RequestParam("decodername") String decodername,
                                    @RequestParam("encoding") String encoding) {
        final DBInterface db = (DBInterface)appContext.getBean(dbname);
        final GraphDeserializer<? extends Graph> decoder =
                (GraphDeserializer) appContext.getBean(decodername);
        final Graph g = decoder.apply(encoding);
        final int id = db.addGraph(g);

        return new RedirectView("/dbs/view/"+dbname);
    }

    @GetMapping({"/dbs/retrieve/{dbname}"})
    public String viewGraph(Map<String, Object> model,
                            @PathVariable("dbname") String dbname,
                            @RequestParam("id") int id) {
        DBInterface db = (DBInterface)appContext.getBean(dbname);
        Graph g = db.retrieveGraph(id, MutableGraph::new);
        Map<String,GraphSerializer> encoders = appContext.getBeansOfType(GraphSerializer.class);
        Set<String> encoderNames = encoders.keySet();
        String encoder = "sai-json-serializer"; // TODO: keep track in session of selection

        model.put("id", id);
        model.put("encoders", encoderNames);
        model.put("encoder", encoder);
        model.put("encoding", encoders.get(encoder).apply(g));
        model.put("dbname", dbname);
        return "viewgraph";
    }

    @PostMapping(value="/dbs/clear/{dbname}")
    public RedirectView clearDB(@PathVariable("dbname") String dbname) {
        final DBInterface db = (DBInterface)appContext.getBean(dbname);
        db.getGraphIDStream().forEach(db::deleteGraph);
        return new RedirectView("/dbs/view/"+dbname);
    }


    @PostMapping(value="/dbs/populate/{dbname}")
    public RedirectView populateDB(@PathVariable("dbname") String dbname,
                                    @RequestParam("populatorname") String populatorname) {
        final DBInterface db = (DBInterface)appContext.getBean(dbname);
        final DBPopulator pop = (DBPopulator)appContext.getBean(populatorname);
        int taskId = taskManager.addTask(pop.apply(db));
        return new RedirectView("/tasks");
    }

}