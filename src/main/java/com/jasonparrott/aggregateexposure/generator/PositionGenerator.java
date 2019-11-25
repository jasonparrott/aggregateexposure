package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Trade;

public interface PositionGenerator {
    Trade createPosition(Client client, MarketValuation valuation) throws RiskCalculationException;
}
