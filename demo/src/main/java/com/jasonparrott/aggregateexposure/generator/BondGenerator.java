package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.TradeAction;
import com.jasonparrott.aggregateexposure.model.trade.BondTrade;
import com.jasonparrott.aggregateexposure.model.trade.Trade;

import java.math.BigDecimal;

public class BondGenerator implements TradeGenerator {
    @Override
    public Trade createTrade(Client client, int securityId) throws RiskCalculationException {
        return new BondTrade(securityId, ProductType.Bond, TradeAction.New, new BigDecimal(200d + (5000 * Math.random())), client);
    }
}
