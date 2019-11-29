package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.model.SecurityGroup;

public interface ValuationMapperFactory {
    SecurityGroupValuationMapper forSecurityGroup(SecurityGroup securityGroup) throws Exception;
}
