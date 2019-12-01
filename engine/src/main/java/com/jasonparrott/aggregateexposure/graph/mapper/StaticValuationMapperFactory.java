package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.model.SecurityGroupId;

import java.util.List;

public class StaticValuationMapperFactory implements ValuationMapperFactory {
    private final List<SecurityGroupValuationMapper> mapperList;

    public StaticValuationMapperFactory(List<SecurityGroupValuationMapper> mapperList) {
        this.mapperList = mapperList;
    }

    @Override
    public SecurityGroupValuationMapper forSecurityGroup(SecurityGroupId securityGroupId) throws Exception {
        return mapperList.stream().filter(m -> m.canMap(securityGroupId))
                .findAny()
                .orElseThrow(() -> new Exception(String.format("Unable to find valuation mapper for %s", securityGroupId)));
    }
}
