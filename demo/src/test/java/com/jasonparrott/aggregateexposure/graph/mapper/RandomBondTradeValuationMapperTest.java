package com.jasonparrott.aggregateexposure.graph.mapper;

import com.google.common.collect.ImmutableList;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
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

import java.util.List;

import static com.jasonparrott.aggregateexposure.ContainedWithinMatcher.containedWithin;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

public class RandomBondTradeValuationMapperTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private SecurityGroupId securityGroup;

    @Mock
    private CalculationNode c1;
    @Mock
    private CalculationNode c2;
    @Mock
    private CalculationNode c3;
    @Mock
    private CalculationNode c4;
    @Mock
    private CalculationNode c5;

    private Graph<CalculationNode, DefaultEdge> graph;

    @Before
    public void setup() {
        graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        graph.addVertex(c1);
        graph.addVertex(c2);
        graph.addVertex(c3);
        graph.addVertex(c4);
        graph.addVertex(c5);
    }

    @Test
    public void testMapperAllowsBondTrade() {
        doReturn(ProductType.Bond).when(securityGroup).getProductType();
        RandomBondTradeValuationMapper mapper = new RandomBondTradeValuationMapper();
        assertThat(mapper.canMap(securityGroup), is(true));
    }

    @Test
    public void testMapperRejectsNonBondTrades() {
        doReturn(ProductType.Swap).when(securityGroup).getProductType();
        RandomBondTradeValuationMapper mapper = new RandomBondTradeValuationMapper();
        assertThat(mapper.canMap(securityGroup), is(false));
    }

    @Test
    public void testMappingBondTradeReturnsValidNodes() {
        RandomBondTradeValuationMapper mapper = new RandomBondTradeValuationMapper();
        List<CalculationNode> nodes = mapper.getInputsForSecurityGroup(securityGroup, graph);
        List<CalculationNode> VALID_NODES = ImmutableList.of(c1, c2, c3, c4, c5);

        assertThat(nodes, is(not(empty())));
        assertThat(nodes, containedWithin(VALID_NODES));
    }
}