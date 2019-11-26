package com.jasonparrott.aggregateexposure.calculators.graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class IntermediateCalculator implements Calculator {
    private CalculationResult result;
    private Set<Consumer<Calculator>> updateNotificationTargets = new HashSet<>();

    @Override
    public CalculationResult getCalculationResult() {
        return result;
    }

    @Override
    public void calculate(CalculationResult[] inputs) {
        result = ()-> Arrays.stream(inputs).mapToDouble(i->i.getResult()).sum();
    }

    @Override
    public void registerForChanges(Consumer<Calculator> consumer) {
        updateNotificationTargets.add(consumer);
    }
}
