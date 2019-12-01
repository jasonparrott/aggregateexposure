package com.jasonparrott.aggregateexposure.model.trade;

import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.TradeAction;

import java.math.BigDecimal;

public interface Trade {
    ProductType getProductType();

    int getSecurityId();

    BigDecimal getSize();

    TradeAction getTradeAction();

    Client getClient();
}
