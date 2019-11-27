package com.jasonparrott.aggregateexposure.calculators.product;

public interface ProductMetrics {
    double getOpenRisk();

    double getIntradayRisk();

    double getIntradayChange(); // change from this calculation
}
