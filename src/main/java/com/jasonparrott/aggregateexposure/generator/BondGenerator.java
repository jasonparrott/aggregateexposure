package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.Client;
import com.jasonparrott.aggregateexposure.model.BondTrade;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Trade;

public class BondGenerator implements PositionGenerator {

    @Override
    public Trade createPosition(Client client, MarketValuation valuation) {
        return new BondTrade(client.getId(), valuation);
    }
}
