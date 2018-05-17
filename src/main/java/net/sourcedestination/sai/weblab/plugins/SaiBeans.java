package net.sourcedestination.sai.weblab.plugins;

import net.sourcedestination.funcles.tuple.Tuple3;
import net.sourcedestination.sai.analysis.ExperimentLogProcessor;
import net.sourcedestination.sai.analysis.GraphMetric;
import net.sourcedestination.sai.analysis.GraphMetricsProcessor;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.learning.ClassificationModel;
import net.sourcedestination.sai.learning.ClassificationModelGenerator;
import net.sourcedestination.sai.retrieval.FeatureIndexBasedRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

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

    public static Supplier<ExperimentLogProcessor> processorFactory(
            Tuple3<String, GraphMetricsProcessor.AggregationType, GraphMetric>... metrics) {
        return () -> new GraphMetricsProcessor(new DBInterfaces(), metrics);
    }
}

class DBInterfaces implements Map<String, DBInterface> {

    @Autowired
    private ApplicationContext appContext;

    @Override
    public int size() {
        return appContext.getBeansOfType(DBInterface.class).size();
    }

    @Override
    public boolean isEmpty() {
        return appContext.getBeansOfType(DBInterface.class).isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return appContext.getBeansOfType(DBInterface.class).containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return appContext.getBeansOfType(DBInterface.class).containsValue(value);
    }

    @Override
    public DBInterface get(Object key) {
        return appContext.getBeansOfType(DBInterface.class).get(key);
    }

    @Override
    public DBInterface put(String key, DBInterface value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DBInterface remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends DBInterface> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return appContext.getBeansOfType(DBInterface.class).keySet();
    }

    @Override
    public Collection<DBInterface> values() {
        return appContext.getBeansOfType(DBInterface.class).values();
    }

    @Override
    public Set<Entry<String, DBInterface>> entrySet() {
        return appContext.getBeansOfType(DBInterface.class).entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}