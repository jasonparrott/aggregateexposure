package com.jasonparrott.aggregateexposure.graph;

import com.jasonparrott.aggregateexposure.TradeUpdateManager;

import java.util.Collection;

public class IntermediateCalculator extends BaseCalculator {
    public IntermediateCalculator(Collection<Calculator> inputs, TradeUpdateManager updateManager) {
        super(inputs, updateManager);
    }

    public void addInput(Calculator input) {
        super.getInputs().add(input);
    }

    @Override
    protected CalculationResult doCalculation() {
        // fake calculation just using the inputs
        return () -> getInputs().stream().mapToDouble(c -> c.getCalculationResult().getResult()).sum();
    }
}
