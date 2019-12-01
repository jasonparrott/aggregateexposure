package com.jasonparrott.aggregateexposure.metrics;

import java.math.BigDecimal;
import java.util.Objects;

public class DefaultProductMetrics implements ProductMetrics {
    private final BigDecimal openRisk;
    private final BigDecimal intradayRisk;
    private final BigDecimal intradayChange;

    public DefaultProductMetrics() {
        this(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public DefaultProductMetrics(BigDecimal openRisk, BigDecimal intradayRisk, BigDecimal intradayChange) {
        this.openRisk = openRisk;
        this.intradayRisk = intradayRisk;
        this.intradayChange = intradayChange;
    }

    @Override
    public BigDecimal getOpenRisk() {
        return openRisk;
    }

    @Override
    public BigDecimal getIntradayRisk() {
        return intradayRisk;
    }

    @Override
    public BigDecimal getIntradayChange() {
        return intradayChange;
    }

    @Override
    public ProductMetrics add(ProductMetrics other) {
        return new DefaultProductMetrics(
                other.getOpenRisk(),
                intradayRisk.add(other.getIntradayChange()),
                intradayChange.add(other.getIntradayChange()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultProductMetrics that = (DefaultProductMetrics) o;
        return Objects.equals(openRisk, that.openRisk) &&
                Objects.equals(intradayRisk, that.intradayRisk) &&
                Objects.equals(intradayChange, that.intradayChange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openRisk, intradayRisk, intradayChange);
    }

    @Override
    public String toString() {
        return "DefaultProductMetrics{" +
                "openRisk=" + openRisk +
                ", intradayRisk=" + intradayRisk +
                ", intradayChange=" + intradayChange +
                '}';
    }
}
