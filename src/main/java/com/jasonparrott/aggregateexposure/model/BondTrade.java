package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.RiskCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class BondTrade extends LinearProduct {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UUID id;
    private final int clientId;

    public BondTrade(MarketValuation marketValuation, TradeAction action, LocalDate today, LocalDate previous, RiskCalculator riskCalculator, UUID id, int clientId) throws RiskCalculationException {
        super(marketValuation, action, today, previous, riskCalculator);
        this.id = id;
        this.clientId = clientId;
    }

    @Override
    public int getClientId() {
        return clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BondTrade bondTrade = (BondTrade) o;
        return clientId == bondTrade.clientId &&
                Objects.equals(id, bondTrade.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, clientId);
    }
}
