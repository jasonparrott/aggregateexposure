package com.jasonparrott.aggregateexposure.graph;

import com.jasonparrott.aggregateexposure.model.MarketValuation;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Set;

public interface GraphUpdateStrategy {
    void update(Graph<CalculationNode, DefaultEdge> graph, Set<CalculationNode> leaves, MarketValuation valuation);
}
