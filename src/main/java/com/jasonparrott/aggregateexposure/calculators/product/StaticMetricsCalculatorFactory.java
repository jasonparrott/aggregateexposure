package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.util.Collection;

public class StaticMetricsCalculatorFactory implements MetricsCalculatorFactory {
    @Override
    public MetricsCalculator forSecurityGroup(SecurityGroup securityGroup, Collection<Calculator> inputs) throws Exception {
        switch (securityGroup.getProductType()) {
            case Bond:
                return new BondMetricsCalculator(inputs);
            case Swap:
                return new SwapMetricsCalculator(inputs);
            default:
                throw new IllegalArgumentException("Unknown product type.");
        }
    }
}
