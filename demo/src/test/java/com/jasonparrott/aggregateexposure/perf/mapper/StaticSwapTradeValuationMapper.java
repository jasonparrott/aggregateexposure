package com.jasonparrott.aggregateexposure.perf.mapper;

import com.google.common.collect.ImmutableList;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.mapper.SecurityGroupValuationMapper;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;

public class StaticSwapTradeValuationMapper implements SecurityGroupValuationMapper {
    @Override
    public List<CalculationNode> getInputsForSecurityGroup(SecurityGroupId securityGroup, Graph graph) {
        ArrayList<CalculationNode> nodeList = new ArrayList<CalculationNode>(graph.vertexSet());
        return ImmutableList.of(nodeList.get(4),
                nodeList.get(13),
                nodeList.get(20));
    }

    @Override
    public boolean canMap(SecurityGroupId securityGroup) {
        return true;
    }
}
