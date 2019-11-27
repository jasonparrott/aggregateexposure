package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Random;

public class OptionMetricsCalculator implements MetricsCalculator {
    private final Random r = new Random();
    private final Collection<Calculator> inputs;

    public OptionMetricsCalculator(Collection<Calculator> inputs) {
        this.inputs = inputs;
    }

    @Override
    public ProductMetrics calculateRisk(Trade trade, LocalDate today, LocalDate previous) {
        double intraday = inputs.stream().mapToDouble(i -> i.getCalculationResult().getResult()).sum() * Math.random();
        return new DefaultProductMetrics(
                0,
                intraday,
                intraday);
    }
}
