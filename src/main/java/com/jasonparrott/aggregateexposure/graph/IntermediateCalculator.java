package com.jasonparrott.aggregateexposure.graph;

import com.jasonparrott.aggregateexposure.SecurityGroupUpdateManager;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class IntermediateCalculator extends BaseCalculator {
    private UUID uuid = UUID.randomUUID();

    public IntermediateCalculator(Collection<Calculator> inputs, SecurityGroupUpdateManager updateManager, String label) {
        super(inputs, updateManager, label);
    }

    public void addInput(Calculator input) {
        super.getInputs().add(input);
    }

    @Override
    protected CalculationResult doCalculation() {
        // fake calculation just using the inputs
        return () -> getInputs().stream().mapToDouble(c -> c.getCalculationResult().getResult()).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IntermediateCalculator that = (IntermediateCalculator) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uuid);
    }
}
