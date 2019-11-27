package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.product.MetricsCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.UUID;

public class BondTrade extends LinearProduct {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UUID id;
    private final int clientId;

    public BondTrade(TradeAction action, LocalDate today, LocalDate previous, MetricsCalculator metricsCalculator, int clientId) throws RiskCalculationException {
        super(action, today, previous, metricsCalculator);
        id = UUID.randomUUID();
        this.clientId = clientId;
    }


    @Override
    public int getClientId() {
        return clientId;
    }
}
