package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.model.SecurityGroupId;

public interface ValuationMapperFactory {
    SecurityGroupValuationMapper forSecurityGroup(SecurityGroupId securityGroupId) throws Exception;
}
