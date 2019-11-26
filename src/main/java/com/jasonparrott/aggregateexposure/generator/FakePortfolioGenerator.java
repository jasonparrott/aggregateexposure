package com.jasonparrott.aggregateexposure.generator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.PortfolioBuilder;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.model.Trade;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class FakePortfolioGenerator implements PortfolioBuilder {
    private final SwapGenerator swapGenerator = new SwapGenerator();
    private final BondGenerator bondGenerator = new BondGenerator();
    private final OptionGenerator optionGenerator = new OptionGenerator();

    private Graph<Calculator, DefaultEdge> riskGraph;
    private List<Calculator> roots;
    private final int minPerAssetClass;
    private final int maxPerAssetClass;

    private final Random r = new Random();

    public FakePortfolioGenerator(Graph<Calculator, DefaultEdge> riskGraph, List<Calculator> roots, int minPerAssetClass, int maxPerAssetClass) {
        this.riskGraph = riskGraph;
        this.roots = roots;
        this.minPerAssetClass = minPerAssetClass;
        this.maxPerAssetClass = maxPerAssetClass;
    }

    @Override
    public List<Trade> getPortfolio(Client client) throws RiskCalculationException {
        List<Trade> trades = new LinkedList<>();
        List<Calculator> calculators = new ArrayList<>(riskGraph.vertexSet());
        AllDirectedPaths<Calculator, DefaultEdge> allDirectedPaths = new AllDirectedPaths<>(riskGraph);
        trades.addAll(createPositions(client, swapGenerator, calculators, roots, allDirectedPaths, minPerAssetClass, maxPerAssetClass));
        trades.addAll(createPositions(client, bondGenerator, calculators, roots, allDirectedPaths, minPerAssetClass, maxPerAssetClass));
        trades.addAll(createPositions(client, optionGenerator, calculators, roots, allDirectedPaths, minPerAssetClass, maxPerAssetClass));
        return trades;
    }

    private Collection<Trade> createPositions(Client client,
                                              PositionGenerator generator,
                                              List<Calculator> allCalculators,
                                              List<Calculator> roots,
                                              AllDirectedPaths<Calculator, DefaultEdge> allDirectedPaths,
                                              int minPerAssetClass,
                                              int maxPerAssetClass) throws RiskCalculationException {
        int count = minPerAssetClass + r.nextInt(maxPerAssetClass - minPerAssetClass);
        LinkedList<Trade> trades = new LinkedList<>();
        for (int i = 0; i < count; ++i) {
            Collection<Calculator> inputs = getRandomCalculators(allCalculators, r.nextInt(4));
            // calculate the interest set of this trade - ie all nodes between the inputs and all root nodes
            // which would be affected by an update to any of the nodes along the way
            Multimap<Calculator, Calculator> interestSetMap = MultimapBuilder.hashKeys().linkedHashSetValues().build();
            for(Calculator input : inputs) {
                List<GraphPath<Calculator, DefaultEdge>> interestSet = allDirectedPaths.getAllPaths(ImmutableSet.of(input), new HashSet<>(roots), true, Integer.MAX_VALUE);
                for(GraphPath<Calculator, DefaultEdge> path : interestSet) {
                    interestSetMap.putAll(input, path.getVertexList());
                }
            }

            Trade trade = generator.createPosition(client, inputs, interestSetMap);
            trades.add(trade);
        }
        return trades;
    }

    private Collection<Calculator> getRandomCalculators(List<Calculator> calculators, int count) {
        List<Calculator> result = new ArrayList<>(count);
        while(result.size() < count) {
            int calculatorIndex = r.nextInt(calculators.size());
            Calculator c = calculators.get(calculatorIndex);
            if (result.contains(c))
                continue;

            result.add(c);
        }

        return result;
    }
}
