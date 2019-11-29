package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;

@ValuationMapperProducer(productType = ProductType.Bond)
public class RandomBondValuationMapperFactory implements ValuationMapperFactory {
    @Override
    public SecurityGroupValuationMapper forSecurityGroup(SecurityGroup securityGroup) throws Exception {
        return new RandomBondTradeValuationMapper();
    }
}
