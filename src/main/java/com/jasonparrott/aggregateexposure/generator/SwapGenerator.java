package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.Client;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Position;
import com.jasonparrott.aggregateexposure.model.SwapPosition;

public class SwapGenerator implements PositionGenerator {
    @Override
    public Position createPosition(Client client, MarketValuation valuation) {
        return new SwapPosition(client.getId(), valuation);
    }
}
