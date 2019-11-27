package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.calculators.product.ProductMetrics;

import java.util.function.Consumer;

public interface Trade {
    int getClientId();

    ProductMetrics getMetrics();
    TradeAction getAction();

    Runnable updateMetrics();
    void updateTradeAction(TradeAction update); // assumption is that underlying trade info has been updated before calling this.

    void registerUpdateCallback(Consumer<Double> callback); // notify that risk has been updated

    void unregisterUpdateCallback(Consumer<Double> callback);
}
