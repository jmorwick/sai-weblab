package net.sourcedestination.sai.weblab.plugins;

import net.sourcedestination.sai.analysis.ExperimentLogProcessor;
import net.sourcedestination.sai.reporting.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RetrievedGraphsList implements ExperimentLogProcessor {

    private List<List> retrievedGraphIds = new ArrayList<>();

    @Override
    public String getPattern() {
        return "retrieved Graph ID #(\\d+) from (.+)$";
    }

    @Override
    public void processLogMessage(String ... groups) {
        retrievedGraphIds.add(List.of(Integer.parseInt(groups[1]), groups[2]));
    }

    @Override
    public Report get() {
        Report model = new Report();
        model.put("retrievedgraphs", retrievedGraphIds);
        model.put("view", "retrieval-report");
        return model;
    }

    public static ExperimentLogProcessor.Factory getFactory() {
        return RetrievedGraphsList::new;
    }

}
