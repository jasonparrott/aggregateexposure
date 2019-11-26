package com.jasonparrott.aggregateexposure.model;

import java.util.function.Consumer;

public interface Trade {
    int getClientId();

    int getOpenRisk();

    int getIntradayRisk();

    TradeAction getAction();

    void updateTradeAction(TradeAction update); // assumption is that underlying trade info has been updated before calling this.

    void registerUpdateCallback(Consumer<Integer> callback);
}
