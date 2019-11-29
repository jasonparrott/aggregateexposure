package com.jasonparrott.aggregateexposure.model;

public interface Trade {
    ProductType getProductType();

    int getSecurityId();

    int getPosition();

    TradeAction getAction();
}
