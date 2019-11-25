package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.OptionCalculator;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.OptionTrade;
import com.jasonparrott.aggregateexposure.model.Trade;
import com.jasonparrott.aggregateexposure.model.TradeAction;

import java.util.UUID;

public class OptionGenerator implements PositionGenerator {

    @Override
    public Trade createPosition(Client client, MarketValuation valuation) throws RiskCalculationException {
        return new OptionTrade(valuation, TradeAction.New, new OptionCalculator(), UUID.randomUUID(), client.getId());
    }
}
