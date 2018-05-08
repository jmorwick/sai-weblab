package net.sourcedestination.sai.weblab.plugins;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.learning.ClassificationModel;
import net.sourcedestination.sai.learning.ClassificationModelGenerator;
import net.sourcedestination.sai.retrieval.FeatureIndexBasedRetriever;

import java.util.Optional;

/**
 * Some static methods from the SAI library are intended to be used as method references
 * which implement some of SAI's functional interfaces. This class returns the
 * method references with factory methods so that they are accessible from the spring xml.
 */
public class SaiBeans {
    public static final FeatureIndexBasedRetriever getRetreiveByBasicFeatureIndexCount() {
        return FeatureIndexBasedRetriever::retreiveByBasicFeatureIndexCount;
    }

    public static final ClassificationModel getCheatingClassifier() {
        return  g -> {
            Optional<Feature> res = g.getFeatures()
                    .filter(f -> f.getName().equals("expected-classification")).findFirst();
            return res.isPresent() ? res.get().getValue() : "";
        };
    }

    public static final ClassificationModelGenerator getCheaterGenerator() {
        return (db, trainingLabels) -> getCheatingClassifier();

    }
}
