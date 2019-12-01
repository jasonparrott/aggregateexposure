package com.jasonparrott.aggregateexposure.metrics;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Random;

public class SwapMetricsCalculator implements MetricsCalculator {
    private final Random r = new Random();
    private final Collection<Calculator> inputs;

    public SwapMetricsCalculator(Collection<Calculator> inputs) {
        Validate.notNull(inputs);
        this.inputs = inputs;
    }

    @Override
    public ProductMetrics calculateRisk(SecurityGroup trades, LocalDate today, LocalDate previous) {
        BigDecimal origionalRisk = trades.getMetrics().getOpenRisk().add(trades.getMetrics().getIntradayRisk());
        BigDecimal intraday = calculateRiskAsOf(today, trades.getAggregatePosition());
        BigDecimal open = calculateRiskAsOf(previous, trades.getAggregatePosition());
        BigDecimal change = (intraday.add(open)).subtract(origionalRisk);

        return new DefaultProductMetrics(open, intraday, change);
    }

    private BigDecimal calculateRiskAsOf(LocalDate today, BigDecimal position) {
        if (inputs == null || inputs.isEmpty())
            return BigDecimal.ZERO;

        return new BigDecimal(inputs.stream().mapToDouble(i -> i.getCalculationResult().getResult()).sum() * Math.random());
    }

    public boolean canCalculate(ProductType productType) {
        return ProductType.Swap.equals(productType);
    }
}