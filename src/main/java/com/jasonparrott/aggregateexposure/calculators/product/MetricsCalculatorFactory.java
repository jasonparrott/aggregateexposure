package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.util.Collection;

public interface MetricsCalculatorFactory {
    MetricsCalculator forSecurityGroup(SecurityGroup securityGroup, Collection<Calculator> inputs) throws Exception;
}
