package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.MarketValuationEngine;
import com.jasonparrott.aggregateexposure.PortfolioBuilder;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FakePortfolioGenerator implements PortfolioBuilder {
    private final SwapGenerator swapGenerator = new SwapGenerator();
    private final BondGenerator bondGenerator = new BondGenerator();
    private final OptionGenerator optionGenerator = new OptionGenerator();

    private final MarketValuationEngine marketValuationEngine;
    private final int minPerAssetClass;
    private final int maxPerAssetClass;

    private final Random r = new Random();

    public FakePortfolioGenerator(MarketValuationEngine marketValuationEngine, int minPerAssetClass, int maxPerAssetClass) {
        this.marketValuationEngine = marketValuationEngine;
        this.minPerAssetClass = minPerAssetClass;
        this.maxPerAssetClass = maxPerAssetClass;
    }

    @Override
    public List<Trade> getPortfolio(Client client) throws RiskCalculationException {
        List<Trade> trades = new LinkedList<>();
        trades.addAll(createPositions(client, swapGenerator, marketValuationEngine, minPerAssetClass, maxPerAssetClass));
        trades.addAll(createPositions(client, bondGenerator, marketValuationEngine, minPerAssetClass, maxPerAssetClass));
        trades.addAll(createPositions(client, optionGenerator, marketValuationEngine, minPerAssetClass, maxPerAssetClass));
        return trades;
    }

    private Collection<Trade> createPositions(Client client,
                                              PositionGenerator generator,
                                              MarketValuationEngine marketValuationEngine,
                                              int minPerAssetClass,
                                              int maxPerAssetClass) throws RiskCalculationException {
        int count = minPerAssetClass + r.nextInt(maxPerAssetClass - minPerAssetClass);
        LinkedList<Trade> trades = new LinkedList<>();
        for (int i = 0; i < count; ++i) {
            Trade trade = generator.createPosition(client, marketValuationEngine.getRandomValulation());
            trades.add(trade);
        }
        return trades;
    }
}
