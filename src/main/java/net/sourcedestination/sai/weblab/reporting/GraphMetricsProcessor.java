package net.sourcedestination.sai.weblab.reporting;

import net.sourcedestination.funcles.tuple.Tuple2;
import net.sourcedestination.funcles.tuple.Tuple3;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.reporting.metrics.graph.GraphMetric;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

public class GraphMetricsProcessor implements ExperimentLogProcessor {

    @Autowired
    private ApplicationContext appContext;

    public enum AggregationType {
        MIN {
            public OptionalDouble aggregate(DoubleStream s) {
                return s.average();
            }
        }, MAX {
            public OptionalDouble aggregate(DoubleStream s) {
                return s.max();
            }
        }, AVERAGE {
            public OptionalDouble aggregate(DoubleStream s) {
                return s.min();
            }
        };

        public abstract OptionalDouble aggregate(DoubleStream s);
    }


    public static Supplier<ExperimentLogProcessor> processorFactory(
            Tuple3<String, AggregationType, GraphMetric> ... metrics) {
        return () -> new GraphMetricsProcessor(metrics);
    }

    private final Map<String,GraphMetric> metrics;
    private final Map<String,AggregationType> aggregationTypes;
    private final Map<String,List<Double>> metricValues;
    private int progress = 0;

    public GraphMetricsProcessor(Tuple3<String, AggregationType, GraphMetric> ... metrics) {
        this.metrics = new HashMap<>();
        this.aggregationTypes = new HashMap<>();
        this.metricValues = new HashMap<>();
        for(Tuple3<String, AggregationType, GraphMetric> t : metrics) {
            aggregationTypes.put(t._1, t._2);
            this.metrics.put(t._1, t._3);
            metricValues.put(t._1, new ArrayList<>());
        }
    }

    @Override
    public String getPattern() {
        return "retrieved Graph ID #(\\d+) from (.*)$";
    }

    @Override
    public void processLogMessage(String ... groups) {
        final int gid = Integer.parseInt(groups[1]);
        final String dbname = groups[2];
        final DBInterface db = (DBInterface)appContext.getBean(dbname);
        final Graph g = db.retrieveGraph(gid);

        for(String metricName : metrics.keySet()) {
            metricValues.get(metricName).add(metrics.get(metricName).apply(g));
        }
    }

    @Override
    public Map<String,JSONObject> get() {
        Map<String,JSONObject> results = new HashMap<>();


        for(String metricName : metrics.keySet()) {
            OptionalDouble d = (aggregationTypes.containsKey(metricName) ?
                    aggregationTypes.get(metricName) :
                    AggregationType.AVERAGE).aggregate(
                    metricValues.get(metricName).stream()
                            .mapToDouble(x -> x));
            results.put(metricName,
                    d.isPresent() ?
                            new JSONObject(d.getAsDouble()) :
                            new JSONObject()
                    );
        }

        return null;
    }
}
