package com.jasonparrott.aggregateexposure.generator;

import com.google.common.collect.Multimap;
import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.product.OptionCalculator;
import com.jasonparrott.aggregateexposure.model.OptionTrade;
import com.jasonparrott.aggregateexposure.model.Trade;
import com.jasonparrott.aggregateexposure.model.TradeAction;

import java.util.Collection;

public class OptionGenerator implements PositionGenerator {

    @Override
    public Trade createPosition(Client client, Collection<Calculator> inputs, Multimap<Calculator, Calculator> interestSet) throws RiskCalculationException {
        return new OptionTrade(inputs, interestSet, TradeAction.New, new OptionCalculator(), client.getId());
    }
}
