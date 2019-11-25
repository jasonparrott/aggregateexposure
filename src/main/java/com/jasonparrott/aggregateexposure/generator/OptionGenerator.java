package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.Client;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.OptionTrade;
import com.jasonparrott.aggregateexposure.model.Trade;

public class OptionGenerator implements PositionGenerator {

    @Override
    public Trade createPosition(Client client, MarketValuation valuation) {
        return new OptionTrade(marketValuation, tradeAction, riskCalculator, client.getId(), valuation);
    }
}
