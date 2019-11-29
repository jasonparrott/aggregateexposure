package com.jasonparrott.aggregateexposure.model;

import java.util.Objects;

public class MarketValuation {
    private final String label;
    private double value;

    public MarketValuation(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void update(double newValue) {
        value = newValue;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketValuation valuation = (MarketValuation) o;
        return Double.compare(valuation.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
