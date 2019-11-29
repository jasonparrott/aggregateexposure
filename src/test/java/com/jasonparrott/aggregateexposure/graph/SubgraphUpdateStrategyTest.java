package com.jasonparrott.aggregateexposure.graph;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.jasonparrott.aggregateexposure.SecurityGroupUpdateManager;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.jasonparrott.aggregateexposure.ContainedWithinMatcher.containedWithin;
import static org.junit.Assert.*;

public class SubgraphUpdateStrategyTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private MarketValuation valuation1 = new MarketValuation("1", 10.0d);
    private MarketValuation valuation2 = new MarketValuation("2", 20.0d);

    @Mock
    private SecurityGroupUpdateManager updateManager;

    private MarketValuationCalculator calculator1 = new MarketValuationCalculator(valuation1, updateManager);
    private CalculationNode calculationNode1 = new CalculationNode(calculator1);
    private MarketValuationCalculator calculator2 = new MarketValuationCalculator(valuation2, updateManager);
    private CalculationNode calculationNode2 = new CalculationNode(calculator2);

    private IntermediateCalculator intermediateCalculator1 = new IntermediateCalculator(ImmutableList.of(calculator1), updateManager, "ic1");
    private CalculationNode calculationNode3 = new CalculationNode(intermediateCalculator1);
    private IntermediateCalculator intermediateCalculator2 = new IntermediateCalculator(ImmutableList.of(intermediateCalculator1, calculator2), updateManager, "ic2");
    private CalculationNode calculationNode4 = new CalculationNode(intermediateCalculator2);
    private IntermediateCalculator intermediateCalculator3 = new IntermediateCalculator(ImmutableList.of(intermediateCalculator2), updateManager, "ic3");
    private CalculationNode calculationNode5 = new CalculationNode(intermediateCalculator3);

    private DirectedAcyclicGraph<CalculationNode, DefaultEdge> graph;
    private DirectedAcyclicGraph<CalculationNode, DefaultEdge> subgraph;

    @Before
    public void setup() {
        graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        graph.addVertex(calculationNode1);
        graph.addVertex(calculationNode2);
        graph.addVertex(calculationNode3);
        graph.addVertex(calculationNode4);
        graph.addVertex(calculationNode5);

        graph.addEdge(calculationNode1, calculationNode3);
        graph.addEdge(calculationNode3, calculationNode4);
        graph.addEdge(calculationNode2, calculationNode4);
        graph.addEdge(calculationNode4, calculationNode5);

        subgraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        subgraph.addVertex(calculationNode2);
        subgraph.addVertex(calculationNode4);
        subgraph.addVertex(calculationNode5);

        subgraph.addEdge(calculationNode2, calculationNode4);
        subgraph.addEdge(calculationNode4, calculationNode5);
    }

    @Test
    public void testCachedSubgraphIsUsedIfAvailable() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        SubgraphUpdateStrategy strategy = new SubgraphUpdateStrategy(service);

        calculationNode2.setSubgraph(subgraph);

        strategy.update(graph, ImmutableSet.of(calculationNode5), valuation2);

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
        assertSame(calculationNode2.getSubgraph(), subgraph);
    }

    @Test
    public void testNewSubgraphIsUsedIfNoCacheIsAvailable() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        SubgraphUpdateStrategy strategy = new SubgraphUpdateStrategy(service);

        strategy.update(graph, ImmutableSet.of(calculationNode5), valuation2);

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
        assertNotSame(calculationNode2.getSubgraph(), subgraph); // not the same object
    }

    @Test
    public void testNewSubgraphHasCorrectPaths() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        SubgraphUpdateStrategy strategy = new SubgraphUpdateStrategy(service);

        strategy.update(graph, ImmutableSet.of(calculationNode5), valuation2);

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
        Graph subgraph = calculationNode2.getSubgraph();
        assertThat(subgraph.vertexSet(), containedWithin(ImmutableSet.of(calculationNode2, calculationNode4, calculationNode5)));
        assertThat(ImmutableSet.of(calculationNode2, calculationNode4, calculationNode5), containedWithin(subgraph.vertexSet()));
    }
}