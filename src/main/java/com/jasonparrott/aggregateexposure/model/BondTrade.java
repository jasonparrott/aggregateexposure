package com.jasonparrott.aggregateexposure.model;

import com.google.common.collect.Multimap;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.RiskCalculator;
import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

public class BondTrade extends LinearProduct {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UUID id;
    private final int clientId;

    public BondTrade(Collection<Calculator> inputs, Multimap<Calculator, Calculator> interstSet, TradeAction action, LocalDate today, LocalDate previous, RiskCalculator riskCalculator, int clientId) throws RiskCalculationException {
        super(inputs, interstSet, action, today, previous, riskCalculator);
        this.id = UUID.randomUUID();
        this.clientId = clientId;
    }

    @Override
    public int getClientId() {
        return 0;
    }
}
