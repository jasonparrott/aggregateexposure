package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public interface SecurityGroupValuationMapper<T extends SecurityGroup> {
    List<CalculationNode> getInputsForSecurityGroup(T securityGroup, Graph<CalculationNode, DefaultEdge> graph);

    boolean canMap(SecurityGroup securityGroup);
}
