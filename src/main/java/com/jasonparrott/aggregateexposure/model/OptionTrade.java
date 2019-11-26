package com.jasonparrott.aggregateexposure.model;

import com.google.common.collect.Multimap;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.calculators.product.OptionCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public class OptionTrade implements Trade {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OptionCalculator riskCalculator;
    private final UUID id;
    private final int clientId;
    private TradeAction tradeAction;
    private Consumer<Integer> updateCallback;

    private int openRisk;
    private int intradayRisk;
    private final Collection<Calculator> inputs;

    public OptionTrade(Collection<Calculator> inputs, Multimap<Calculator, Calculator> interestSet, TradeAction tradeAction, OptionCalculator riskCalculator, int clientId) throws RiskCalculationException {
        this.tradeAction = tradeAction;
        this.riskCalculator = riskCalculator;
        this.id =UUID.randomUUID();
        this.clientId = clientId;
        this.inputs = inputs;
        for(Calculator c : interestSet.values()) {
            c.registerForChanges(this::sourceChanged);
        }

        setIntradayRisk(calculateRisk());
    }

    @Override
    public void updateTradeAction(TradeAction update) {
        tradeAction = update;
        try {
            switch (update) {
                case New:
                case LateBooked:
                case Amend:
                case Reset:
                    setIntradayRisk(calculateRisk());
                    break;
                case EarlyBooked:
                    // do nothing
                    return;
                case Cancel:
                    setIntradayRisk(-1 * intradayRisk);
                    break;
            }
            int difference = (getOpenRisk() + getIntradayRisk());
            updateCallback.accept(difference);
        } catch (RiskCalculationException rce) {
            logger.warn("Error calculating risk.", rce);
        }
    }

    protected int calculateRisk() throws RiskCalculationException {
        return riskCalculator.calculateRisk(this, LocalDate.now(), inputs);
    }

    public void sourceChanged(Calculator calculator) {
        try {
            setIntradayRisk(calculateRisk());
            int difference = (getOpenRisk() + getIntradayRisk());
            updateCallback.accept(difference);
        } catch (RiskCalculationException rce) {
            logger.warn("Error calculating risk.", rce);
        }
    }

    @Override
    public int getClientId() {
        return clientId;
    }

    @Override
    public int getOpenRisk() {
        return openRisk;
    }

    protected void setOpenRisk(int openRisk) {
        this.openRisk = openRisk;
    }

    @Override
    public int getIntradayRisk() {
        return intradayRisk;
    }

    protected void setIntradayRisk(int intradayRisk) {
        this.intradayRisk = intradayRisk;
    }

    @Override
    public TradeAction getAction() {
        return tradeAction;
    }

    @Override
    public void registerUpdateCallback(Consumer<Integer> callback) {
        this.updateCallback = callback;
    }
}
