package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.calculators.product.MetricsMapper;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.GraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.mapper.SecurityGroupValuationMapper;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RiskEngine {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Graph<CalculationNode, DefaultEdge> graph;
    private final GraphUpdateStrategy graphUpdateStrategy;
    private final ExecutorService executor;
    private final Collection<SecurityGroupValuationMapper> valuationMappers;
    private final MetricsMapper metricsMapper;
    private final Set<CalculationNode> leaves;

    private List<Client> clients = new LinkedList<Client>();

    public RiskEngine(Graph<CalculationNode, DefaultEdge> graph, GraphUpdateStrategy graphUpdateStrategy, Collection<SecurityGroupValuationMapper> valuationMappers, SecurityGroupUpdateManager securityGroupUpdateManager, MetricsMapper metricsMapper) {
        this(graph, graphUpdateStrategy, valuationMappers, securityGroupUpdateManager, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), metricsMapper);
    }

    public RiskEngine(Graph<CalculationNode, DefaultEdge> graph, GraphUpdateStrategy graphUpdateStrategy, Collection<SecurityGroupValuationMapper> valuationMappers, SecurityGroupUpdateManager securityGroupUpdateManager, ExecutorService executor, MetricsMapper metricsMapper) {
        this.graph = graph;
        this.executor = executor;
        this.graphUpdateStrategy = graphUpdateStrategy;
        this.valuationMappers = valuationMappers;
        this.metricsMapper = metricsMapper;

        // calculate all the leaves
        leaves = new LinkedHashSet<>();
        for (CalculationNode c : graph.vertexSet()) {
            try {
                if (graph.outDegreeOf(c) == 0)
                    leaves.add(c);
            } catch (Exception e) {
                // TODO: remove
            }
        }
    }

    public Future<?> updateValuation(MarketValuation valuation) {
        return graphUpdateStrategy.update(graph, leaves, valuation);
    }

    public void addClient(Client client) {
        for (SecurityGroup group : client.getTrades()) {
            Optional<SecurityGroupValuationMapper> mapper = valuationMappers.stream().filter(m -> m.canMap(group)).findAny();
            if (!mapper.isPresent()) {
                logger.warn(String.format("Could not find any valuation mapper for trade: %s", group.toString()));
                continue;
            }

            List<CalculationNode> inputs = mapper.get().getInputsForSecurityGroup(group, graph);
            group.setMetricsCalculator(metricsMapper.getMetrics(group.getProductType(), inputs));

            inputs.forEach(i -> i.getCalculator().registerForChanges(group));
        }

        clients.add(client);
    }
}
