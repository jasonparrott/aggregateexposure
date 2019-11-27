package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.product.SwapMetricsCalculator;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.SwapTrade;
import com.jasonparrott.aggregateexposure.model.Trade;
import com.jasonparrott.aggregateexposure.model.TradeAction;

import java.time.LocalDate;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class SwapGenerator implements PositionGenerator {
    private static LocalDate TODAY = LocalDate.of(2019, 11, 13);
    private static LocalDate PREV = LocalDate.of(2019, 11, 13);

    @Override
    public Trade createPosition(Client client, Collection<CalculationNode> inputs) throws RiskCalculationException {
        return new SwapTrade(TradeAction.New, TODAY, PREV, new SwapMetricsCalculator(inputs.stream().map(CalculationNode::getCalculator).collect(toList())), client.getId());
    }
}
