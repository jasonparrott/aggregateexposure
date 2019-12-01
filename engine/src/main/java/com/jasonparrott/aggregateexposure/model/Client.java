package com.jasonparrott.aggregateexposure.model;

import java.util.Objects;

public class Client {
    private final int id;
    private final CreditRating creditRating;
//    private SecurityGroup[] trades;

//    private FenwickTree valuationTree;

    public Client(int id, CreditRating creditRating) {
        this.id = id;
        this.creditRating = creditRating;
    }

    public int getId() {
        return id;
    }

    public CreditRating getCreditRating() {
        return creditRating;
    }

    //    public SecurityGroup[] getTrades() {
//        return trades;
//    }

//    public void setTrades(SecurityGroup[] trades) {
//        this.trades = trades;
//        for (int i = 0; i < trades.length; ++i) {
//            int finalI = i;
//            trades[i].registerUpdateCallback((diff) -> {
//                try {
//                    valuationTree.update(finalI, (ProductMetrics) diff);
//                } catch (InterruptedException e) {
//                    Thread.interrupted();
//                }
//            });
//        }
//        valuationTree = new FenwickTree(trades);
//    }

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
