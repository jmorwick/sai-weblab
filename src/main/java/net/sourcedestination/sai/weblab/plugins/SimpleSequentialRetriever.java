package net.sourcedestination.sai.weblab.plugins;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.retrieval.GraphRetriever;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * Created by jmorwick on 8/14/17.
 */
@Component("SimpleSequentialRetreiver")
public class SimpleSequentialRetriever implements GraphRetriever {

    @Override
    public Stream<Integer> retrieve(DBInterface db, Graph graph) {
        return db.getGraphIDStream();
    }
}
