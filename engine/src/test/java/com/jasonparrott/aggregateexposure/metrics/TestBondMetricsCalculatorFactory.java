package com.jasonparrott.aggregateexposure.metrics;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;

import java.time.LocalDate;
import java.util.Collection;

@MetricsCalculatorProducer(productType = ProductType.Bond)
public class TestBondMetricsCalculatorFactory implements MetricsCalculatorFactory {
    @Override
    public MetricsCalculator forSecurityGroup(SecurityGroupId securityGroupId, Collection<Calculator> inputs) throws Exception {
        return new TestBondMetricsCalculator();
    }

    public static class TestBondMetricsCalculator implements MetricsCalculator {
        @Override
        public ProductMetrics calculateRisk(SecurityGroup securityGroup, LocalDate today, LocalDate previous) {
            return null;
        }
    }
}
