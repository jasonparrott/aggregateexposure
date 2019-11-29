package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.util.Collection;

@MetricsCalculatorProducer(productType = ProductType.Bond)
public class BondMetricsCalculatorFactory implements MetricsCalculatorFactory {

    public BondMetricsCalculatorFactory() {
    }

    @Override
    public MetricsCalculator forSecurityGroup(SecurityGroup securityGroup, Collection<Calculator> inputs) throws Exception {
        return new BondMetricsCalculator(inputs);
    }
}
