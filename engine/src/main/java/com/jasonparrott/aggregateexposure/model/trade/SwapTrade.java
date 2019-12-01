package com.jasonparrott.aggregateexposure.model.trade;

import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.TradeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;

public class SwapTrade implements Trade {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int securityId;
    private final ProductType productType;
    private final TradeAction tradeAction;
    private final BigDecimal size;
    private final Client client;

    public SwapTrade(int securityId, ProductType productType, TradeAction tradeAction, BigDecimal size, Client client) {
        this.securityId = securityId;
        this.productType = productType;
        this.tradeAction = tradeAction;
        this.size = size;
        this.client = client;
    }

    @Override
    public int getSecurityId() {
        return securityId;
    }

    @Override
    public ProductType getProductType() {
        return productType;
    }

    @Override
    public TradeAction getTradeAction() {
        return tradeAction;
    }

    @Override
    public BigDecimal getSize() {
        return size;
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public String toString() {
        return "BondTrade{" +
                "securityId=" + securityId +
                ", productType=" + productType +
                ", tradeAction=" + tradeAction +
                ", size=" + size +
                ", client=" + client +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwapTrade bondTrade = (SwapTrade) o;
        return securityId == bondTrade.securityId &&
                productType == bondTrade.productType &&
                tradeAction == bondTrade.tradeAction &&
                Objects.equals(size, bondTrade.size) &&
                Objects.equals(client, bondTrade.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(securityId, productType, tradeAction, size, client);
    }

}
