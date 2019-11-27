package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.SwapTrade;
import com.jasonparrott.aggregateexposure.model.Trade;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomSwapTradeValuationMapper implements TradeValuationMapper<SwapTrade> {
    private Graph<CalculationNode, DefaultEdge> graph;
    private Random r = new Random();

    @Override
    public List<CalculationNode> getInputsForTrade(SwapTrade trade) {
        List<CalculationNode> results = new LinkedList<>();

        int count = r.nextInt(3);
        Object[] vertexes = graph.vertexSet().toArray();
        for (int i = 0; i < count; ++i) {
            results.add((CalculationNode) vertexes[r.nextInt(vertexes.length)]);
        }

        return results;
    }

    @Override
    public boolean canMap(Trade trade) {
        return (trade instanceof SwapTrade);
    }
}
