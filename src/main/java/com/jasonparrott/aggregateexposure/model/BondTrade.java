package com.jasonparrott.aggregateexposure.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BondTrade implements Trade {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int securityId;
    private final ProductType productType;

    private TradeAction tradeAction;
    private int position;

    public BondTrade(int securityId, ProductType productType, TradeAction tradeAction, int position) {
        this.securityId = securityId;
        this.productType = productType;
        this.tradeAction = tradeAction;
        this.position = position;
    }

    @Override
    public int getSecurityId() {
        return securityId;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public TradeAction getAction() {
        return null;
    }

    @Override
    public ProductType getProductType() {
        return productType;
    }
}
