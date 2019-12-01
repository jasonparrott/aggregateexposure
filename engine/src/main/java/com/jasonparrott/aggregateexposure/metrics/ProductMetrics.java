package com.jasonparrott.aggregateexposure.metrics;

import java.math.BigDecimal;

public interface ProductMetrics {
    BigDecimal getOpenRisk();

    BigDecimal getIntradayRisk();

    BigDecimal getIntradayChange();

    ProductMetrics add(ProductMetrics other);
}
