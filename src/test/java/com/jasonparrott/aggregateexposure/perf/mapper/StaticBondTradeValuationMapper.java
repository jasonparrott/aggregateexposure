package com.jasonparrott.aggregateexposure.perf.mapper;

import com.google.common.collect.ImmutableList;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.mapper.SecurityGroupValuationMapper;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;

public class StaticBondTradeValuationMapper implements SecurityGroupValuationMapper {
    @Override
    public List<CalculationNode> getInputsForSecurityGroup(SecurityGroup securityGroup, Graph graph) {
        ArrayList<CalculationNode> nodeList = new ArrayList<CalculationNode>(graph.vertexSet());
        return ImmutableList.of(nodeList.get(2),
                nodeList.get(8),
                nodeList.get(17));
    }

    @Override
    public boolean canMap(SecurityGroup securityGroup) {
        return true;
    }
}
