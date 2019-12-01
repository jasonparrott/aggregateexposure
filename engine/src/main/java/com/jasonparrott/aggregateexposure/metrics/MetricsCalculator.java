package com.jasonparrott.aggregateexposure.metrics;

import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.time.LocalDate;

public interface MetricsCalculator {
    ProductMetrics calculateRisk(SecurityGroup securityGroup, LocalDate today, LocalDate previous);
}
