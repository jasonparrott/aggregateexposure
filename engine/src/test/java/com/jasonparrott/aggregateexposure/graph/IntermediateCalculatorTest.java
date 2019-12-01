package com.jasonparrott.aggregateexposure.graph;

import com.google.common.collect.ImmutableSet;
import com.jasonparrott.aggregateexposure.SecurityGroupUpdateManager;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class IntermediateCalculatorTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private SecurityGroupUpdateManager securityGroupUpdateManager;

    private MarketValuation valuation = new MarketValuation(UUID.randomUUID().toString(), 10d);
    private MarketValuationCalculator marketValuationCalculator = new MarketValuationCalculator(valuation, securityGroupUpdateManager);

    private IntermediateCalculator intermediateCalculator = new IntermediateCalculator(ImmutableSet.of(marketValuationCalculator), securityGroupUpdateManager, "IC");
    private CalculationNode intermediateNode = new CalculationNode(intermediateCalculator);

    @Test
    public void testContains() {
        Graph<CalculationNode, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        graph.addVertex(intermediateNode);

        assertThat(graph.vertexSet(), contains(intermediateNode));
    }


}