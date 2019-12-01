package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.graph.GraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.mapper.SecurityGroupValuationMapper;
import com.jasonparrott.aggregateexposure.graph.mapper.ValuationMapperFactory;
import com.jasonparrott.aggregateexposure.metrics.DefaultProductMetrics;
import com.jasonparrott.aggregateexposure.metrics.MetricsCalculatorFactory;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import com.jasonparrott.aggregateexposure.model.SecurityGroupImpl;
import com.jasonparrott.aggregateexposure.model.position.Position;
import com.jasonparrott.aggregateexposure.model.trade.Trade;
import com.jasonparrott.aggregateexposure.partitioner.Partitioner;
import com.jasonparrott.aggregateexposure.position.PositionService;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.stream.Collectors.toList;

public class RiskEngine {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Graph<CalculationNode, DefaultEdge> graph;
    private final GraphUpdateStrategy graphUpdateStrategy;
    private final ValuationMapperFactory valuationMapperFactory;
    private final MetricsCalculatorFactory metricsCalculatorFactory;
    private final Partitioner partitioner;
    private final PositionService positionService;
    private final Set<CalculationNode> leaves;
    private final UpdatePublisher updatePublisher;

    private boolean shutdown = false;
    private AtomicLong tradeCount = new AtomicLong(0L);


    private final ReentrantLock positionMapLock = new ReentrantLock();
    private final Map<SecurityGroupId, SecurityGroup> positionMap = new ConcurrentHashMap<>();

    public RiskEngine(Graph<CalculationNode, DefaultEdge> graph, GraphUpdateStrategy graphUpdateStrategy, SecurityGroupUpdateManager securityGroupUpdateManager, ValuationMapperFactory valuationMapperFactory, MetricsCalculatorFactory metricsCalculatorFactory, Partitioner partitioner, TradeListener tradeListener, PositionService positionService, UpdatePublisher updatePublisher) {
        this(graph, graphUpdateStrategy, securityGroupUpdateManager, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), valuationMapperFactory, metricsCalculatorFactory, partitioner, positionService, tradeListener, updatePublisher);
    }

    public RiskEngine(Graph<CalculationNode, DefaultEdge> graph, GraphUpdateStrategy graphUpdateStrategy, SecurityGroupUpdateManager securityGroupUpdateManager, ExecutorService executor, ValuationMapperFactory valuationMapperFactory, MetricsCalculatorFactory metricsCalculatorFactory, Partitioner partitioner, PositionService positionService, TradeListener tradeListener, UpdatePublisher updatePublisher) {
        this.graph = graph;
        this.graphUpdateStrategy = graphUpdateStrategy;
        this.valuationMapperFactory = valuationMapperFactory;
        this.metricsCalculatorFactory = metricsCalculatorFactory;
        this.partitioner = partitioner;
        this.positionService = positionService;
        this.updatePublisher = updatePublisher;
        executor.submit(() -> {
            while (!shutdown) {
                for (Trade trade : tradeListener.get()) {
                    try {
                        addTrade(trade);
                        tradeCount.getAndIncrement();
                    } catch (RiskEngineException ree) {
                        logger.error("Unable to add trade to risk engine!", ree);
                    }
                }
            }
        });

        // calculate all the leaves
        leaves = new LinkedHashSet<>();
        for (CalculationNode c : graph.vertexSet()) {
            if (graph.outDegreeOf(c) == 0)
                leaves.add(c);
        }
    }

    public long getTradeCount() {
        return tradeCount.get();
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public Future<?> updateValuation(MarketValuation valuation) {
        return graphUpdateStrategy.update(graph, leaves, valuation);
    }

    private void addTrade(Trade trade) throws RiskEngineException {
        Position position = positionService.getPositionForTrade(trade);
        SecurityGroupId securityGroupId = partitioner.findGroupForPosition(position);
        positionMapLock.lock();
        try {
            SecurityGroupValuationMapper mapper = valuationMapperFactory.forSecurityGroup(securityGroupId);
            List<CalculationNode> inputs = mapper.getInputsForSecurityGroup(securityGroupId, graph);
            List<Calculator> inputCalculators = inputs.stream().map(CalculationNode::getCalculator).collect(toList());

            if (!positionMap.containsKey(securityGroupId)) {
                SecurityGroup group = new SecurityGroupImpl(securityGroupId,
                        metricsCalculatorFactory.forSecurityGroup(securityGroupId, inputCalculators),
                        new DefaultProductMetrics());
                positionMap.put(securityGroupId, group);

                mapper.getInputsForSecurityGroup(securityGroupId, graph)
                        .stream().map(CalculationNode::getCalculator)
                        .forEach(c -> c.registerForChanges(group));
                group.registerUpdateCallback(updatePublisher);
            }

            positionMap.get(securityGroupId).add(position);

        } catch (Exception e) {
            throw new RiskEngineException("Unable to add position to engine.", e, position);
        } finally {
            positionMapLock.unlock();
        }
    }
}
