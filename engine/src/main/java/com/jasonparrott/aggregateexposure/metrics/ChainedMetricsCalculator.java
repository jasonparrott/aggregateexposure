package com.jasonparrott.aggregateexposure.metrics;

import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.time.LocalDate;
import java.util.List;

public class ChainedMetricsCalculator implements MetricsCalculator {
    private final List<MetricsCalculator> calculatorList;

    public ChainedMetricsCalculator(List<MetricsCalculator> calculatorList) {
        this.calculatorList = calculatorList;
    }

    @Override
    public ProductMetrics calculateRisk(SecurityGroup securityGroup, LocalDate today, LocalDate previous) {
        ProductMetrics metrics = new DefaultProductMetrics();
        for (MetricsCalculator calculator : calculatorList) {
            metrics = metrics.add(calculator.calculateRisk(securityGroup, today, previous));
        }

        return metrics;
    }
}
