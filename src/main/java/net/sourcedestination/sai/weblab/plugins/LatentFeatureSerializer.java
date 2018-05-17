package net.sourcedestination.sai.weblab.plugins;

import net.sourcedestination.sai.analysis.GraphMetric;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphSerializer;

import java.util.Arrays;
import java.util.List;

public class LatentFeatureSerializer implements GraphSerializer {

    private final List<GraphMetric> metrics;

    public LatentFeatureSerializer(GraphMetric ... metrics) {
        this.metrics = Arrays.asList(metrics);
    }

    public String apply(Graph g) {
        return metrics.stream()  // convert to csv line
                .map( metric -> ""+metric.apply(g))
                .reduce((x,y) -> x+","+y)
                .get();
    }
}
