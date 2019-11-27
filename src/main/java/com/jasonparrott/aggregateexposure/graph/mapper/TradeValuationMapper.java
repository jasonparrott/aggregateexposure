package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.util.List;

public interface TradeValuationMapper<T extends Trade> {
    List<CalculationNode> getInputsForTrade(T trade);

    boolean canMap(Trade trade);
}
