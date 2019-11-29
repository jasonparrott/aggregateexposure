package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import org.jgrapht.Graph;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomBondTradeValuationMapper implements SecurityGroupValuationMapper {
    private Random r = new Random();

    @Override
    public List<CalculationNode> getInputsForSecurityGroup(SecurityGroup securityGroup, Graph graph) {
        List<CalculationNode> results = new LinkedList<>();

        int count = r.nextInt(3) + 1;
        Object[] vertexes = graph.vertexSet().toArray();
        for (int i = 0; i < count; ++i) {
            results.add((CalculationNode) vertexes[r.nextInt(vertexes.length)]);
        }

        return results;
    }

    @Override
    public boolean canMap(SecurityGroup securityGroup) {
        return ProductType.Bond.equals(securityGroup.getProductType());
    }
}
