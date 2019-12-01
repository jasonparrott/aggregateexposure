package com.jasonparrott.aggregateexposure.metrics;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;

import java.util.Collection;

public class StaticMetricsCalculatorFactory implements MetricsCalculatorFactory {
    @Override
    public MetricsCalculator forSecurityGroup(SecurityGroupId securityGroupId, Collection<Calculator> inputs) throws Exception {
        switch (securityGroupId.getProductType()) {
            case Bond:
                return new BondMetricsCalculator(inputs);
            case Swap:
                return new SwapMetricsCalculator(inputs);
            default:
                throw new IllegalArgumentException("Unknown product type.");
        }
    }
}
