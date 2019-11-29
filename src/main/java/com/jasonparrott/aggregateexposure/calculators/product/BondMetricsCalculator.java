package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Random;

public class BondMetricsCalculator implements MetricsCalculator {
    private final Random r = new Random();
    private final Collection<Calculator> inputs;

    public BondMetricsCalculator(Collection<Calculator> inputs) {
        Validate.notNull(inputs);
        this.inputs = inputs;
    }

    @Override
    public ProductMetrics calculateRisk(SecurityGroup trades, LocalDate today, LocalDate previous) {
        double origionalRisk = trades.getMetrics().getOpenRisk() + trades.getMetrics().getIntradayRisk();
        double intraday = calculateRiskAsOf(today, trades.getAggregatePosition());
        double open = calculateRiskAsOf(previous, trades.getAggregatePosition());
        double change = (intraday + open) - origionalRisk;

        return new DefaultProductMetrics(open, intraday, change);
    }

    private double calculateRiskAsOf(LocalDate today, long position) {
        if (inputs == null || inputs.isEmpty())
            return 0.0d;

        return inputs.stream().mapToDouble(i -> i.getCalculationResult().getResult()).sum() * Math.random();
    }

    public boolean canCalculate(ProductType productType) {
        return ProductType.Bond.equals(productType);
    }
}