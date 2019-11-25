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

    private final TradeAction tradeAction;
    private final OptionCalculator riskCalculator;

    private MarketValuation marketValuation;
    private final UUID id;
    private final int clientId;
    private Consumer<Integer> updateCallback;

    private int openRisk;
    private int intradayRisk;

    public OptionTrade(MarketValuation marketValuation, TradeAction tradeAction, OptionCalculator riskCalculator, UUID id, int clientId) {
        this.marketValuation = marketValuation;
        this.tradeAction = tradeAction;
        this.riskCalculator = riskCalculator;
        this.id = id;
        this.clientId = clientId;
    }

    @Override
    public void updateTrade(Trade updatedTrade) {
        int originalRisk = getOpenRisk() + getIntradayRisk();
        try {
            switch (updatedTrade.getAction()) {
                case New:
                case LateBooked:
                case Amend:
                case Reset:
                    setIntradayRisk(calculateRisk(this));
                    originalRisk = 0;
                    break;
                case EarlyBooked:
                    // do nothing
                    break;
                case Cancel:
                    setIntradayRisk(-1 * intradayRisk);
                    break;
            }
        } catch (RiskCalculationException rce) {
            logger.warn("Error calculating risk.", rce);
        } finally {
            int difference = originalRisk - (getOpenRisk() + getIntradayRisk());
            updateCallback.accept(difference);
        }
    }

    protected int calculateRisk(Trade trade) throws RiskCalculationException {
        return riskCalculator.calculateRisk(this, LocalDate.now(), marketValuation);
    }

    @Override
    public void updateMarketValuation(MarketValuation newValuation) {
        marketValuation = newValuation;
        int originalRisk = getOpenRisk() + getIntradayRisk();
        try {
            setIntradayRisk(calculateRisk(this));
            setOpenRisk(0);
        } catch (RiskCalculationException rce) {
            logger.warn("Error calculating risk.", rce);
        } finally {
            int difference = originalRisk - (getOpenRisk() + getIntradayRisk());
            updateCallback.accept(difference);
        }
    }

    @Override
    public int getClientId() {
        return 0;
    }

    @Override
    public int getOpenRisk() {
        return openRisk;
    }

    @Override
    public int getIntradayRisk() {
        return intradayRisk;
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

    protected void setIntradayRisk(int intradayRisk) {
        this.intradayRisk = intradayRisk;
    }

    protected void setOpenRisk(int openRisk) {
        this.openRisk = openRisk;
    }
}
