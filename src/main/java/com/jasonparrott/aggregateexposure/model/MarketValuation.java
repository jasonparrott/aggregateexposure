package com.jasonparrott.aggregateexposure.model;

public class MarketValuation {
    private int value;

    public MarketValuation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void update(int newValue) {
        value = newValue;
    }
}
