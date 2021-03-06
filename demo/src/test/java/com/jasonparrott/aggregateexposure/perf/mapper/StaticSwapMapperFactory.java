package com.jasonparrott.aggregateexposure.perf.mapper;

import com.jasonparrott.aggregateexposure.graph.mapper.SecurityGroupValuationMapper;
import com.jasonparrott.aggregateexposure.graph.mapper.ValuationMapperFactory;
import com.jasonparrott.aggregateexposure.graph.mapper.ValuationMapperProducer;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;

@ValuationMapperProducer(productType = ProductType.Swap)
public class StaticSwapMapperFactory implements ValuationMapperFactory {
    @Override
    public SecurityGroupValuationMapper forSecurityGroup(SecurityGroupId securityGroup) throws Exception {
        return new StaticSwapTradeValuationMapper();
    }
}
