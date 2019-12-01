package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.metrics.ProductMetrics;
import com.jasonparrott.aggregateexposure.model.position.Position;

import java.math.BigDecimal;
import java.util.function.Consumer;

public interface SecurityGroup {
    SecurityGroupId getId();

    ProductMetrics getMetrics();

    BigDecimal getAggregatePosition();

    boolean add(Position position);

    boolean remove(Object o);

    Runnable updateMetrics();

    void unregisterUpdateCallback(Consumer<SecurityGroup> callback);

    void registerUpdateCallback(Consumer<SecurityGroup> callback);
}
