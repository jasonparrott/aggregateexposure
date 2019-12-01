package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.util.function.Consumer;

/**
 * notified
 */
public interface UpdatePublisher extends Consumer<SecurityGroup> {
}
