package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.Client;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Trade;
import com.jasonparrott.aggregateexposure.model.SwapTrade;

public class SwapGenerator implements PositionGenerator {
    @Override
    public Trade createPosition(Client client, MarketValuation valuation) {
        return new SwapTrade(client.getId(), valuation);
    }
}
