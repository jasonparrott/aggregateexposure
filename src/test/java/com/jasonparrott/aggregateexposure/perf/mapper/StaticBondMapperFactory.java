package com.jasonparrott.aggregateexposure.perf.mapper;

import com.jasonparrott.aggregateexposure.graph.mapper.SecurityGroupValuationMapper;
import com.jasonparrott.aggregateexposure.graph.mapper.ValuationMapperFactory;
import com.jasonparrott.aggregateexposure.graph.mapper.ValuationMapperProducer;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;

@ValuationMapperProducer(productType = ProductType.Bond)
public class StaticBondMapperFactory implements ValuationMapperFactory {
    @Override
    public SecurityGroupValuationMapper forSecurityGroup(SecurityGroup securityGroup) throws Exception {
        return new StaticBondTradeValuationMapper();
    }
}
