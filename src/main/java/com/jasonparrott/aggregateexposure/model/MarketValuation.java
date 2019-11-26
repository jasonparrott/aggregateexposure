package com.jasonparrott.aggregateexposure.model;

public class MarketValuation {
    private double value;

    public MarketValuation(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void update(double newValue) {
        value = newValue;
    }
}
