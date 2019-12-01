package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public interface SecurityGroupValuationMapper {
    List<CalculationNode> getInputsForSecurityGroup(SecurityGroupId securityGroupId, Graph<CalculationNode, DefaultEdge> graph);

    boolean canMap(SecurityGroupId securityGroup);
}
