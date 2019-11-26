package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.time.LocalDate;
import java.util.Collection;

public interface RiskCalculator {
    int calculateRisk(Trade trade, LocalDate asOf, Collection<Calculator> inputs);
}
