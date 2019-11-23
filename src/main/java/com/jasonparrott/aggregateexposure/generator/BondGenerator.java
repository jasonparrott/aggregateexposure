package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.Client;
import com.jasonparrott.aggregateexposure.model.BondPosition;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Position;

public class BondGenerator implements PositionGenerator {

    @Override
    public Position createPosition(Client client, MarketValuation valuation) {
        return new BondPosition(client.getId(), valuation);
    }
}
