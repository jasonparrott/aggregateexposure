package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.Trade;

import java.util.Objects;

public class Client {
    private final int id;
    private Trade[] trades;
    private FenwickTree valuationTree;

    public Client(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Trade[] getTrades() {
        return trades;
    }

    public void setTrades(Trade[] trades) {
        this.trades = trades;
        for(int i = 0; i < trades.length; ++i) {
            int finalI = i;
            trades[i].registerUpdateCallback((diff) -> {
                try {
                    valuationTree.update(finalI, diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }
        valuationTree = new FenwickTree(trades);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
