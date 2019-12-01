package com.jasonparrott.aggregateexposure.metrics;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;

import java.util.Collection;

public interface MetricsCalculatorFactory {
    MetricsCalculator forSecurityGroup(SecurityGroupId securityGroupId, Collection<Calculator> inputs) throws Exception;
}
