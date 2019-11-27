package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.product.BondMetricsCalculator;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.BondTrade;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.Trade;
import com.jasonparrott.aggregateexposure.model.TradeAction;

import java.time.LocalDate;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class BondGenerator implements PositionGenerator {
    private static LocalDate TODAY = LocalDate.of(2019, 11, 13);
    private static LocalDate PREV = LocalDate.of(2019, 11, 13);

    @Override
    public Trade createPosition(Client client, Collection<CalculationNode> inputs) throws RiskCalculationException {
        return new BondTrade(TradeAction.New, TODAY, PREV, new BondMetricsCalculator(inputs.stream().map(CalculationNode::getCalculator).collect(toList())), client.getId());
    }
}
