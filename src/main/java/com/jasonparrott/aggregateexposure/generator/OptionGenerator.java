package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.Client;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.OptionPosition;
import com.jasonparrott.aggregateexposure.model.Position;

public class OptionGenerator implements PositionGenerator {

    @Override
    public Position createPosition(Client client, MarketValuation valuation) {
        return new OptionPosition(client.getId(), valuation);
    }
}
