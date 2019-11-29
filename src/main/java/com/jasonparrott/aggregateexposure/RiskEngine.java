package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.calculators.product.MetricsCalculatorFactory;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.GraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.mapper.SecurityGroupValuationMapper;
import com.jasonparrott.aggregateexposure.graph.mapper.ValuationMapperFactory;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;

public class RiskEngine {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Graph<CalculationNode, DefaultEdge> graph;
    private final GraphUpdateStrategy graphUpdateStrategy;
    private final ExecutorService executor;
    private final ValuationMapperFactory valuationMapperFactory;
    private final MetricsCalculatorFactory metricsCalculatorFactory;
    private final Set<CalculationNode> leaves;

    private List<Client> clients = new LinkedList<Client>();

    public RiskEngine(Graph<CalculationNode, DefaultEdge> graph, GraphUpdateStrategy graphUpdateStrategy, SecurityGroupUpdateManager securityGroupUpdateManager, ValuationMapperFactory valuationMapperFactory, MetricsCalculatorFactory metricsCalculatorFactory) {
        this(graph, graphUpdateStrategy, securityGroupUpdateManager, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), valuationMapperFactory, metricsCalculatorFactory);
    }

    public RiskEngine(Graph<CalculationNode, DefaultEdge> graph, GraphUpdateStrategy graphUpdateStrategy, SecurityGroupUpdateManager securityGroupUpdateManager, ExecutorService executor, ValuationMapperFactory valuationMapperFactory, MetricsCalculatorFactory metricsCalculatorFactory) {
        this.graph = graph;
        this.executor = executor;
        this.graphUpdateStrategy = graphUpdateStrategy;
        this.valuationMapperFactory = valuationMapperFactory;
        this.metricsCalculatorFactory = metricsCalculatorFactory;

        // calculate all the leaves
        leaves = new LinkedHashSet<>();
        for (CalculationNode c : graph.vertexSet()) {
            if (graph.outDegreeOf(c) == 0)
                leaves.add(c);
        }
    }

    public Future<?> updateValuation(MarketValuation valuation) {
        return graphUpdateStrategy.update(graph, leaves, valuation);
    }

    public void addClient(Client client) {
        for (SecurityGroup group : client.getTrades()) {
            try {
                SecurityGroupValuationMapper mapper = valuationMapperFactory.forSecurityGroup(group);

                List<CalculationNode> inputs = mapper.getInputsForSecurityGroup(group, graph);
                group.setMetricsCalculator(metricsCalculatorFactory.forSecurityGroup(group, inputs.stream().map(i -> i.getCalculator()).collect(toList())));

                inputs.forEach(i -> i.getCalculator().registerForChanges(group));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        clients.add(client);
    }
}
