package com.jasonparrott.aggregateexposure.model;

import java.time.LocalDate;
import java.util.function.Consumer;

public interface Trade {
    int getClientId();
    int getOpenRisk();
    int getIntradayRisk();
    TradeAction getAction();
    void updateTrade(Trade updatedTrade);
    void updateMarketValuation(MarketValuation newValuation);
    MarketValuation getMarketValuation();
    void registerUpdateCallback(Consumer<Integer> callback);
}
