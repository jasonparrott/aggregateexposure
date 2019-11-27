package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.GraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.mapper.TradeValuationMapper;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Trade;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RiskEngine {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Graph<CalculationNode, DefaultEdge> graph;
    private final GraphUpdateStrategy graphUpdateStrategy;
    private final ExecutorService executor;
    private final Collection<TradeValuationMapper> valuationMappers;
    private final Set<CalculationNode> leaves;

    private List<Client> clients = new LinkedList<Client>();

    public RiskEngine(Graph<CalculationNode, DefaultEdge> graph, GraphUpdateStrategy graphUpdateStrategy, Collection<TradeValuationMapper> valuationMappers, TradeUpdateManager tradeUpdateManager) {
        this(graph, graphUpdateStrategy, valuationMappers, tradeUpdateManager, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    public RiskEngine(Graph<CalculationNode, DefaultEdge> graph, GraphUpdateStrategy graphUpdateStrategy, Collection<TradeValuationMapper> valuationMappers, TradeUpdateManager tradeUpdateManager, ExecutorService executor) {
        this.graph = graph;
        this.executor = executor;
        this.graphUpdateStrategy = graphUpdateStrategy;
        this.valuationMappers = valuationMappers;

        // calculate all the leaves
        leaves = new LinkedHashSet<>();
        for (CalculationNode c : graph.vertexSet()) {
            if (graph.outDegreeOf(c) == 0)
                leaves.add(c);
        }
    }

    public void updateValuation(MarketValuation valuation) {
        graphUpdateStrategy.update(graph, leaves, valuation);
    }

    public void addClient(Client client) {
        clients.add(client);
        for (Trade trade : client.getTrades()) {
            Optional<TradeValuationMapper> mapper = valuationMappers.stream().filter(m -> m.canMap(trade)).findAny();
            if (!mapper.isPresent()) {
                logger.warn(String.format("Could not find any valuation mapper for trade: %s", trade.toString()));
                continue;
            }

            List<CalculationNode> inputs = mapper.get().getInputsForTrade(trade);
            inputs.forEach(i -> i.getCalculator().registerForChanges(trade));
        }
    }
}
