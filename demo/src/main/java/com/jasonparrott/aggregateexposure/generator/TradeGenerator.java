package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.trade.Trade;

public interface TradeGenerator {
    Trade createTrade(Client client, int securityId) throws RiskCalculationException;
}
