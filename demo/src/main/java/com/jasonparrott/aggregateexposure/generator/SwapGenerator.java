package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.TradeAction;
import com.jasonparrott.aggregateexposure.model.trade.SwapTrade;
import com.jasonparrott.aggregateexposure.model.trade.Trade;

import java.math.BigDecimal;

public class SwapGenerator implements TradeGenerator {
    @Override
    public Trade createTrade(Client client, int securityId) throws RiskCalculationException {
        return new SwapTrade(securityId, ProductType.Swap, TradeAction.New, new BigDecimal(200000d + (5000000 * Math.random())), client);
    }
}
