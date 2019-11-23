package com.jasonparrott.aggregateexposure.model;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class SwapPosition implements Position {
    private final UUID id;
    private final int clientId;
    private final MarketValuation marketValuation;

    private int exposure;
    private Consumer<Integer> updateCallback;

    public SwapPosition(int clientId, MarketValuation marketValuation) {
        this.clientId = clientId;
        this.marketValuation = marketValuation;
        id = UUID.randomUUID();
    }

    public SwapPosition(int clientId, MarketValuation marketValuation, int initialExposure) {
        this.clientId = clientId;
        this.marketValuation = marketValuation;
        id = UUID.randomUUID();
        exposure = initialExposure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwapPosition position = (SwapPosition) o;
        return clientId == position.clientId &&
                Objects.equals(id, position.id) &&
                Objects.equals(marketValuation, position.marketValuation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, marketValuation);
    }

    @Override
    public int getClientId() {
        return 0;
    }

    public int getExposure() {
        return exposure;
    }

    public void setExposure(int exposure) {
        int difference = exposure - this.exposure;
        this.exposure = exposure;
        updateCallback.accept(difference);
    }

    @Override
    public void registerUpdateCallback(Consumer<Integer> callback) {
        this.updateCallback = callback;
    }

    @Override
    public MarketValuation getMarketValuation() {
        return marketValuation;
    }
}
