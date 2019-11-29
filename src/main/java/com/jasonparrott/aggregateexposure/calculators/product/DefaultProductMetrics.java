package com.jasonparrott.aggregateexposure.calculators.product;

public class DefaultProductMetrics implements ProductMetrics {
    private final double openRisk;
    private final double intradayRisk;
    private final double intradayChange;

    public DefaultProductMetrics(double openRisk, double intradayRisk, double intradayChange) {
        this.openRisk = openRisk;
        this.intradayRisk = intradayRisk;
        this.intradayChange = intradayChange;
    }

    @Override
    public double getOpenRisk() {
        return openRisk;
    }

    @Override
    public double getIntradayRisk() {
        return intradayRisk;
    }

    @Override
    public double getIntradayChange() {
        return intradayChange;
    }
}
