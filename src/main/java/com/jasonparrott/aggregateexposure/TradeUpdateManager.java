package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.Trade;

public interface TradeUpdateManager {
    void updateTrade(Trade trade);
}
