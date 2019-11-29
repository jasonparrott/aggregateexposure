package com.jasonparrott.aggregateexposure.perf;

import com.jasonparrott.aggregateexposure.PortfolioBuilder;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.generator.BondGenerator;
import com.jasonparrott.aggregateexposure.generator.PositionGenerator;
import com.jasonparrott.aggregateexposure.generator.SwapGenerator;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.Trade;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class StaticPortfolioGenerator implements PortfolioBuilder {
    private final SwapGenerator swapGenerator = new SwapGenerator();
    private final BondGenerator bondGenerator = new BondGenerator();

    private final Graph<CalculationNode, DefaultEdge> calculationGraph;
    private final int assetsPerClass;
    private final int securities;

    public StaticPortfolioGenerator(Graph<CalculationNode, DefaultEdge> calculationGraph, int assertsPerClass, int securities) {
        this.calculationGraph = calculationGraph;
        this.assetsPerClass = assertsPerClass;
        this.securities = securities;
    }

    @Override
    public List<Trade> getPortfolio(Client client) throws RiskCalculationException {
        List<Trade> trades = new LinkedList<>();
        List<CalculationNode> calculators = new ArrayList<>(calculationGraph.vertexSet());
        trades.addAll(createPositions(client, swapGenerator, calculators, assetsPerClass));
        trades.addAll(createPositions(client, bondGenerator, calculators, assetsPerClass));
        return trades;
    }

    private Collection<Trade> createPositions(Client client,
                                              PositionGenerator generator,
                                              List<CalculationNode> allCalculators,
                                              int assetsPerClass) throws RiskCalculationException {
        LinkedList<Trade> trades = new LinkedList<>();

        for (int i = 0; i < assetsPerClass; ++i) {
            int securityId = i % securities;
            Trade trade = generator.createPosition(client, securityId, 100 + (100 * i));
            trades.add(trade);
        }
        return trades;
    }
}
