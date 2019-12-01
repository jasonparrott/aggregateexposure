package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.trade.Trade;

import java.util.List;

public interface PortfolioBuilder {
    List<Trade> getPortfolio(Client client) throws RiskCalculationException;
}
