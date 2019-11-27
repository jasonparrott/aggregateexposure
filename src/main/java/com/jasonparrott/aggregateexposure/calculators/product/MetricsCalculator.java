package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.model.Trade;

import java.time.LocalDate;

public interface MetricsCalculator {
    ProductMetrics calculateRisk(Trade trade, LocalDate today, LocalDate previous);
}
