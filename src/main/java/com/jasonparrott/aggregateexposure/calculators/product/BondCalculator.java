package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.RiskCalculator;
import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Random;

public class BondCalculator implements RiskCalculator {
    private final Random r = new Random();

    @Override
    public int calculateRisk(Trade trade, LocalDate asOf, Collection<Calculator> inputs) {
        return (int) (inputs.stream().mapToDouble(i->i.getCalculationResult().getResult()).sum() * Math.random());
    }
}
