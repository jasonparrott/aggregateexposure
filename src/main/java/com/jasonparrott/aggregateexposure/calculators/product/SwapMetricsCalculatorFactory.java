package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.util.Collection;

@MetricsCalculatorProducer(productType = ProductType.Swap)
public class SwapMetricsCalculatorFactory implements MetricsCalculatorFactory {

    public SwapMetricsCalculatorFactory() {
    }

    @Override
    public MetricsCalculator forSecurityGroup(SecurityGroup securityGroup, Collection<Calculator> inputs) throws Exception {
        return new SwapMetricsCalculator(inputs);
    }
}
