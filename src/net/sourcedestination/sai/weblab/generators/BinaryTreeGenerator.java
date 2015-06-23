package net.sourcedestination.sai.weblab.generators;

import java.util.Iterator;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.MutableGraph;

public class BinaryTreeGenerator implements Supplier<Graph> {
	private int size;
	private int nodeFeatures;
	private int edgeFeatures;

	public BinaryTreeGenerator(int size, int nodeFeatures, int edgeFeatures) {
		this.size = size;
		this.nodeFeatures = nodeFeatures;
		this.edgeFeatures = edgeFeatures;
	}

	private Feature getEdgeFeature() {
		return new Feature("test", "" + ((int) (Math.random() * edgeFeatures)));
	}

	private Feature getNodeFeature() {
		return new Feature("test", "" + ((int) (Math.random() * nodeFeatures)));
	}

	@Override
	public Graph get() {
		int nid = 1;
		Multimap<Integer, Integer> nodesToEdges = HashMultimap.create();
		MutableGraph g = new MutableGraph();
		g.addNode(nid);
		g.addNodeFeature(nid, getNodeFeature());

		// add nodes/edges
		while (nid < size) {
			// look for position to insert new node
			int currentNode = 1;
			while (nodesToEdges.get(currentNode).size() != 0) {
				Iterator<Integer> edgeIDs = nodesToEdges.get(currentNode)
						.iterator();
				currentNode = g.getEdgeTargetNodeID(edgeIDs.next());
				if (Math.random() > 0.5) { // 50% chance of stopping here or
											// take the other edge
					if (edgeIDs.hasNext())
						currentNode = g.getEdgeTargetNodeID(edgeIDs.next());
					else
						break;
				}
			}
			g.addNode(nid);
			g.addNodeFeature(nid, getNodeFeature());
			g.addEdgeFeature(nid - 1, getEdgeFeature());
			g.addEdge(nid - 1, currentNode, nid);
			nid++;
		}
		return g;
	}
}
