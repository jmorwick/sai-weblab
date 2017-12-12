package net.sourcedestination.sai.weblab.plugins;

import net.sourcedestination.sai.retrieval.FeatureIndexBasedRetriever;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Some static methods from the SAI library are intended to be used as method references
 * which implement some of SAI's functional interfaces. This class returns the
 * method references with factory methods so that they are accessible from the spring xml.
 */
public class SaiBeans {
    public static final FeatureIndexBasedRetriever getRetreiveByBasicFeatureIndexCount() {
        return FeatureIndexBasedRetriever::retreiveByBasicFeatureIndexCount;
    }
}
