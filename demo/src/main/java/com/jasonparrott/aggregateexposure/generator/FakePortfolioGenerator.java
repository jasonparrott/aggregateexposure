package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.PortfolioBuilder;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.trade.Trade;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class FakePortfolioGenerator implements PortfolioBuilder {
    private final SwapGenerator swapGenerator = new SwapGenerator();
    private final BondGenerator bondGenerator = new BondGenerator();
    private final int minPerAssetClass;
    private final int maxPerAssetClass;
    private final int securities;
    private final Random r = new Random();
    private Graph<CalculationNode, DefaultEdge> riskGraph;

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
                                              TradeGenerator generator,
                                              List<CalculationNode> allCalculators,
                                              int minPerAssetClass,
                                              int maxPerAssetClass) throws RiskCalculationException {
        int count = minPerAssetClass + r.nextInt(maxPerAssetClass - minPerAssetClass);
        LinkedList<Trade> trades = new LinkedList<>();
        int securityId = r.nextInt(securities);
        System.out.println("Creating trade with security ID: " + securityId);
        for (int i = 0; i < count; ++i) {
            Trade trade = generator.createTrade(client, securityId);
            trades.add(trade);
        }
        return trades;
    }
}
