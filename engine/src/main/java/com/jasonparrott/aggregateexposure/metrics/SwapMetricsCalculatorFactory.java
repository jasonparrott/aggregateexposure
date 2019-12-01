package com.jasonparrott.aggregateexposure.metrics;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;

import java.util.Collection;

@MetricsCalculatorProducer(productType = ProductType.Swap)
public class SwapMetricsCalculatorFactory implements MetricsCalculatorFactory {

    public SwapMetricsCalculatorFactory() {
    }

    @Override
    public MetricsCalculator forSecurityGroup(SecurityGroupId securityGroup, Collection<Calculator> inputs) throws Exception {
        return new SwapMetricsCalculator(inputs);
    }
}
