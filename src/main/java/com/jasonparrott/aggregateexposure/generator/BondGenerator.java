package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.BondCalculator;
import com.jasonparrott.aggregateexposure.model.BondTrade;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Trade;
import com.jasonparrott.aggregateexposure.model.TradeAction;

import java.time.LocalDate;
import java.util.UUID;

public class BondGenerator implements PositionGenerator {
    private static LocalDate TODAY = LocalDate.of(2019, 11, 13);
    private static LocalDate PREV = LocalDate.of(2019, 11, 13);

    @Override
    public Trade createPosition(Client client, MarketValuation valuation) throws RiskCalculationException {
        return new BondTrade(valuation, TradeAction.New, TODAY, PREV, new BondCalculator(), UUID.randomUUID(), client.getId());
    }
}
