package com.jasonparrott.aggregateexposure.generator;

import com.google.common.collect.Multimap;
import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.util.Collection;

public interface PositionGenerator {
    Trade createPosition(Client client, Collection<Calculator> calculator, Multimap<Calculator, Calculator> interestSetMap) throws RiskCalculationException;
}
