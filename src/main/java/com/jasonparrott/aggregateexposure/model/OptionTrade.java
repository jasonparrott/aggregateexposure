package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.OptionCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Consumer;

public class OptionTrade implements Trade {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OptionCalculator riskCalculator;
    private final UUID id;
    private final int clientId;
    private TradeAction tradeAction;
    private MarketValuation marketValuation;
    private Consumer<Integer> updateCallback;

    private int openRisk;
    private int intradayRisk;

    public OptionTrade(MarketValuation marketValuation, TradeAction tradeAction, OptionCalculator riskCalculator, UUID id, int clientId) throws RiskCalculationException {
        this.marketValuation = marketValuation;
        this.tradeAction = tradeAction;
        this.riskCalculator = riskCalculator;
        this.id = id;
        this.clientId = clientId;
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
        return riskCalculator.calculateRisk(this, LocalDate.now(), marketValuation);
    }

    @Override
    public void updateMarketValuation(MarketValuation newValuation) {
        marketValuation = newValuation;
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
    public MarketValuation getMarketValuation() {
        return marketValuation;
    }

    @Override
    public void registerUpdateCallback(Consumer<Integer> callback) {
        this.updateCallback = callback;
    }
}
