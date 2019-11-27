package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.util.Collection;

public interface PositionGenerator {
    Trade createPosition(Client client, Collection<CalculationNode> calculationNodes) throws RiskCalculationException;
}
