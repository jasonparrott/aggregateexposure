package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.Trade;

import java.util.concurrent.ExecutorService;

public class DelegatingExecutorTradeUpdateManager implements TradeUpdateManager {
    private final ExecutorService executorService;

    public DelegatingExecutorTradeUpdateManager(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void updateTrade(Trade trade) {
        executorService.submit(trade.updateMetrics());
    }
}
