package com.jasonparrott.aggregateexposure.graph;

import com.jasonparrott.aggregateexposure.TradeUpdateManager;
import com.jasonparrott.aggregateexposure.model.MarketValuation;

import java.util.Collections;
import java.util.Objects;

public class MarketValuationCalculator extends BaseCalculator {
    private final MarketValuation valuation;

    public MarketValuationCalculator(MarketValuation valuation, TradeUpdateManager updateManager) {
        super(Collections.emptyList(), updateManager); // allows us to shortcut interating and just call the valuation
        this.valuation = valuation;
    }

    @Override
    protected CalculationResult doCalculation() {
        return () -> valuation.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketValuationCalculator that = (MarketValuationCalculator) o;
        return Objects.equals(valuation, that.valuation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valuation);
    }
}
