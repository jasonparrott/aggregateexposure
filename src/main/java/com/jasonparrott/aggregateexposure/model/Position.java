package com.jasonparrott.aggregateexposure.model;

import java.util.function.Consumer;

public interface Position {
    int getClientId();
    int getExposure();
    void setExposure(int exposure);
    MarketValuation getMarketValuation();
    void registerUpdateCallback(Consumer<Integer> callback);
}
