package com.jasonparrott.aggregateexposure.partitioner;

import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import com.jasonparrott.aggregateexposure.model.position.Position;

public interface Partitioner {
    SecurityGroupId findGroupForPosition(Position position);
}
