package net.sourcedestination.sai.weblab.plugins;

import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphSerializer;
import net.sourcedestination.sai.reporting.metrics.IndependentDBMetric;

import java.util.Arrays;
import java.util.List;

public class LatentFeatureSerializer implements GraphSerializer {

    private final List<IndependentDBMetric> metrics;

    public LatentFeatureSerializer(IndependentDBMetric ... metrics) {
        this.metrics = Arrays.asList(metrics);
    }

    public String apply(Graph g) {
        return metrics.stream()  // convert to csv line
                .map( metric -> ""+metric.processGraph(g))
                .reduce((x,y) -> x+","+y)
                .get();
    }
}
