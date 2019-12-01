package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;

@ValuationMapperProducer(productType = ProductType.Bond)
public class RandomBondValuationMapperFactory implements ValuationMapperFactory {
    @Override
    public SecurityGroupValuationMapper forSecurityGroup(SecurityGroupId securityGroup) throws Exception {
        return new RandomBondTradeValuationMapper();
    }
}
