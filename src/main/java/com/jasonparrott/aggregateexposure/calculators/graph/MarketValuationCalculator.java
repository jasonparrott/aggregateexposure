package com.jasonparrott.aggregateexposure.calculators.graph;

import com.jasonparrott.aggregateexposure.model.MarketValuation;

import java.util.*;
import java.util.function.Consumer;

public class MarketValuationCalculator implements Calculator {
    private final MarketValuation valuation;
    private CalculationResult result;
    private Set<Consumer<Calculator>> updateNotificationTargets = new HashSet<>();

    public MarketValuationCalculator(MarketValuation valuation) {
        this.valuation = valuation;
    }

    @Override
    public void calculate(CalculationResult[] inputs) {
        result = () -> Arrays.stream(inputs).mapToDouble(i->i.getResult()).average().getAsDouble() * valuation.getValue();
    }

    @Override
    public void registerForChanges(Consumer<Calculator> consumer) {
        updateNotificationTargets.add(consumer);
    }

    @Override
    public CalculationResult getCalculationResult() {
        return result;
    }
}
