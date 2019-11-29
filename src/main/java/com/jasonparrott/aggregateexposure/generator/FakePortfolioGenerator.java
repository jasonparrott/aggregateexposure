package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.PortfolioBuilder;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.Trade;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class FakePortfolioGenerator implements PortfolioBuilder {
    private final SwapGenerator swapGenerator = new SwapGenerator();
    private final BondGenerator bondGenerator = new BondGenerator();

    private Graph<CalculationNode, DefaultEdge> riskGraph;
    private final int minPerAssetClass;
    private final int maxPerAssetClass;
    private final int securities;

    private final Random r = new Random();

    public FakePortfolioGenerator(Graph<CalculationNode, DefaultEdge> riskGraph, int minPerAssetClass, int maxPerAssetClass, int securities) {
        this.riskGraph = riskGraph;
        this.minPerAssetClass = minPerAssetClass;
        this.maxPerAssetClass = maxPerAssetClass;
        this.securities = securities;
    }

    @Override
    public List<Trade> getPortfolio(Client client) throws RiskCalculationException {
        List<Trade> trades = new LinkedList<>();
        List<CalculationNode> calculators = new ArrayList<>(riskGraph.vertexSet());
        trades.addAll(createPositions(client, swapGenerator, calculators, minPerAssetClass, maxPerAssetClass));
        trades.addAll(createPositions(client, bondGenerator, calculators, minPerAssetClass, maxPerAssetClass));
        return trades;
    }

    private Collection<Trade> createPositions(Client client,
                                              PositionGenerator generator,
                                              List<CalculationNode> allCalculators,
                                              int minPerAssetClass,
                                              int maxPerAssetClass) throws RiskCalculationException {
        int count = minPerAssetClass + r.nextInt(maxPerAssetClass - minPerAssetClass);
        LinkedList<Trade> trades = new LinkedList<>();
        int securityId = r.nextInt(securities);
        for (int i = 0; i < count; ++i) {
            Collection<CalculationNode> inputs = getRandomCalculators(allCalculators, r.nextInt(4));
            Trade trade = generator.createPosition(client, securityId, 100 + r.nextInt(10000));
            trades.add(trade);
        }
        return trades;
    }

    private Collection<CalculationNode> getRandomCalculators(List<CalculationNode> calculators, int count) {
        List<CalculationNode> result = new ArrayList<>(count);
        while(result.size() < count) {
            int calculatorIndex = r.nextInt(calculators.size());
            CalculationNode c = calculators.get(calculatorIndex);
            if (result.contains(c))
                continue;

            result.add(c);
        }

        return result;
    }
}
