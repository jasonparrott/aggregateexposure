package com.jasonparrott.aggregateexposure.graph;

import com.jasonparrott.aggregateexposure.TradeUpdateManager;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.util.Collection;

public interface Calculator {
    CalculationResult getCalculationResult();

    void calculate();

    Collection<Calculator> getInputs();

    void registerForChanges(Trade listener);

    void unregisterForChanges(Trade listener);

    TradeUpdateManager getUpdateManager();
}
