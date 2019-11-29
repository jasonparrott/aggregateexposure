package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.time.LocalDate;
import java.util.Collection;

public interface MetricsCalculator {
    ProductMetrics calculateRisk(SecurityGroup securityGroup, LocalDate today, LocalDate previous);

    static MetricsCalculator newInstance(Collection<CalculationNode> inputs) {
        return null;
    }
}
