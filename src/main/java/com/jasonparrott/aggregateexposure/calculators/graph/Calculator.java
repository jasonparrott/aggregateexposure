package com.jasonparrott.aggregateexposure.calculators.graph;

import java.util.function.Consumer;

public interface Calculator {
    CalculationResult getCalculationResult();
    void calculate(CalculationResult[] inputs);
    void registerForChanges(Consumer<Calculator> consumer);
}
