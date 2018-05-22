package net.sourcedestination.sai.weblab.plugins;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.DBPopulator;

import java.util.stream.Stream;

public class DBCopier extends DBPopulator {

    private final DBInterface db;

    public DBCopier(DBInterface db) {
        this.db = db;
    }

    @Override
    public Stream<Graph> getGraphStream() {
        return db.getGraphIDStream().map(db::retrieveGraph);
    }

    @Override
    public int getNumGraphs() {
        return db.getDatabaseSize();
    }
}
