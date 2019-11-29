package com.jasonparrott.aggregateexposure.graph;

import com.jasonparrott.aggregateexposure.SecurityGroupUpdateManager;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.util.Collection;

public interface Calculator {
    CalculationResult getCalculationResult();
    void calculate();
    Collection<Calculator> getInputs();

    String getLabel();

    void registerForChanges(SecurityGroup listener);

    void unregisterForChanges(SecurityGroup listener);

    SecurityGroupUpdateManager getUpdateManager();
}
