package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

@ValuationMapperProducer(productType = ProductType.Bond)
public class TestBondValuationMapperFactory implements ValuationMapperFactory {
    @Override
    public SecurityGroupValuationMapper forSecurityGroup(SecurityGroupId securityGroupId) throws Exception {
        return new TestBondMapper();
    }

    public static class TestBondMapper implements SecurityGroupValuationMapper {
        @Override
        public List<CalculationNode> getInputsForSecurityGroup(SecurityGroupId securityGroupId, Graph<CalculationNode, DefaultEdge> graph) {
            return null;
        }

        @Override
        public boolean canMap(SecurityGroupId securityGroup) {
            return ProductType.Bond.equals(securityGroup.getProductType());
        }
    }
}
