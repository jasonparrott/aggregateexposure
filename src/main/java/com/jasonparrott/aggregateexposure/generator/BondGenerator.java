package com.jasonparrott.aggregateexposure.generator;

import com.google.common.collect.Multimap;
import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.product.BondCalculator;
import com.jasonparrott.aggregateexposure.model.BondTrade;
import com.jasonparrott.aggregateexposure.model.Trade;
import com.jasonparrott.aggregateexposure.model.TradeAction;

import java.time.LocalDate;
import java.util.Collection;

public class BondGenerator implements PositionGenerator {
    private static LocalDate TODAY = LocalDate.of(2019, 11, 13);
    private static LocalDate PREV = LocalDate.of(2019, 11, 13);

    @Override
    public Trade createPosition(Client client, Collection<Calculator> inputs, Multimap<Calculator, Calculator> interestSet) throws RiskCalculationException {
        return new BondTrade(inputs, interestSet, TradeAction.New, TODAY, PREV, new BondCalculator(), client.getId());
    }
}
