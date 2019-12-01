package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.SecurityGroupUpdateManager;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.IntermediateCalculator;
import com.jasonparrott.aggregateexposure.graph.MarketValuationCalculator;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.Arrays;
import java.util.Collection;

public class StaticGraphGenerator {

    public static Graph<CalculationNode, DefaultEdge> generateGraph(Collection<MarketValuation> valuations, SecurityGroupUpdateManager securityGroupUpdateManager) {
        if (valuations.size() != 8)
            throw new IllegalArgumentException("Requires 8 market valuations.");

        MarketValuation[] vals = valuations.toArray(new MarketValuation[8]);
        MarketValuationCalculator mv1 = new MarketValuationCalculator(vals[0], securityGroupUpdateManager);
        CalculationNode cn1 = new CalculationNode(mv1);
        MarketValuationCalculator mv2 = new MarketValuationCalculator(vals[1], securityGroupUpdateManager);
        CalculationNode cn2 = new CalculationNode(mv2);
        MarketValuationCalculator mv3 = new MarketValuationCalculator(vals[2], securityGroupUpdateManager);
        CalculationNode cn3 = new CalculationNode(mv3);
        MarketValuationCalculator mv4 = new MarketValuationCalculator(vals[3], securityGroupUpdateManager);
        CalculationNode cn4 = new CalculationNode(mv4);
        MarketValuationCalculator mv8 = new MarketValuationCalculator(vals[4], securityGroupUpdateManager);
        CalculationNode cn8 = new CalculationNode(mv8);
        MarketValuationCalculator mv10 = new MarketValuationCalculator(vals[5], securityGroupUpdateManager);
        CalculationNode cn10 = new CalculationNode(mv10);
        MarketValuationCalculator mv16 = new MarketValuationCalculator(vals[6], securityGroupUpdateManager);
        CalculationNode cn16 = new CalculationNode(mv16);
        MarketValuationCalculator mv19 = new MarketValuationCalculator(vals[7], securityGroupUpdateManager);
        CalculationNode cn19 = new CalculationNode(mv19);

        Graph<CalculationNode, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        IntermediateCalculator ic5 = new IntermediateCalculator(Arrays.asList(mv1), securityGroupUpdateManager, "ic5");
        CalculationNode cn5 = new CalculationNode(ic5);
        IntermediateCalculator ic6 = new IntermediateCalculator(Arrays.asList(mv1, mv2), securityGroupUpdateManager, "ic6");
        CalculationNode cn6 = new CalculationNode(ic6);
        IntermediateCalculator ic7 = new IntermediateCalculator(Arrays.asList(mv2), securityGroupUpdateManager, "ic7");
        CalculationNode cn7 = new CalculationNode(ic7);
        IntermediateCalculator ic9 = new IntermediateCalculator(Arrays.asList(mv3), securityGroupUpdateManager, "ic9");
        CalculationNode cn9 = new CalculationNode(ic9);
        IntermediateCalculator ic11 = new IntermediateCalculator(Arrays.asList(mv10), securityGroupUpdateManager, "ic11");
        CalculationNode cn11 = new CalculationNode(ic11);
        IntermediateCalculator ic12 = new IntermediateCalculator(Arrays.asList(mv4, ic5), securityGroupUpdateManager, "ic12");
        CalculationNode cn12 = new CalculationNode(ic12);
        IntermediateCalculator ic13 = new IntermediateCalculator(Arrays.asList(ic7), securityGroupUpdateManager, "ic13");
        CalculationNode cn13 = new CalculationNode(ic13);
        IntermediateCalculator ic14 = new IntermediateCalculator(Arrays.asList(ic7, mv8), securityGroupUpdateManager, "ic14");
        CalculationNode cn14 = new CalculationNode(ic14);
        IntermediateCalculator ic15 = new IntermediateCalculator(Arrays.asList(ic12, ic6), securityGroupUpdateManager, "ic15");
        CalculationNode cn15 = new CalculationNode(ic15);
        IntermediateCalculator ic17 = new IntermediateCalculator(Arrays.asList(mv16, ic13), securityGroupUpdateManager, "ic17");
        CalculationNode cn17 = new CalculationNode(ic17);
        IntermediateCalculator ic18 = new IntermediateCalculator(Arrays.asList(ic9, mv19), securityGroupUpdateManager, "ic18");
        CalculationNode cn18 = new CalculationNode(ic18);
        IntermediateCalculator ic20 = new IntermediateCalculator(Arrays.asList(ic15, ic17), securityGroupUpdateManager, "ic20");
        CalculationNode cn20 = new CalculationNode(ic20);
        IntermediateCalculator ic21 = new IntermediateCalculator(Arrays.asList(ic14, ic18, ic11), securityGroupUpdateManager, "ic21");
        CalculationNode cn21 = new CalculationNode(ic21);

        graph.addVertex(cn1);
        graph.addVertex(cn2);
        graph.addVertex(cn3);
        graph.addVertex(cn4);
        graph.addVertex(cn5);
        graph.addVertex(cn6);
        graph.addVertex(cn7);
        graph.addVertex(cn8);
        graph.addVertex(cn9);
        graph.addVertex(cn10);
        graph.addVertex(cn11);
        graph.addVertex(cn12);
        graph.addVertex(cn13);
        graph.addVertex(cn14);
        graph.addVertex(cn15);
        graph.addVertex(cn16);
        graph.addVertex(cn17);
        graph.addVertex(cn18);
        graph.addVertex(cn19);
        graph.addVertex(cn20);
        graph.addVertex(cn21);

        graph.addEdge(cn1, cn5);
        graph.addEdge(cn1, cn6);
        graph.addEdge(cn2, cn6);
        graph.addEdge(cn2, cn7);
        graph.addEdge(cn3, cn9);
        graph.addEdge(cn3, cn9);
        graph.addEdge(cn4, cn12);
        graph.addEdge(cn5, cn12);
        graph.addEdge(cn6, cn15);
        graph.addEdge(cn7, cn13);
        graph.addEdge(cn7, cn14);
        graph.addEdge(cn8, cn14);
        graph.addEdge(cn9, cn18);
        graph.addEdge(cn10, cn11);
        graph.addEdge(cn11, cn21);
        graph.addEdge(cn12, cn15);
        graph.addEdge(cn16, cn17);
        graph.addEdge(cn13, cn17);
        graph.addEdge(cn14, cn21);
        graph.addEdge(cn19, cn18);
        graph.addEdge(cn18, cn21);
        graph.addEdge(cn15, cn20);
        graph.addEdge(cn17, cn20);

        return graph;
    }
}
