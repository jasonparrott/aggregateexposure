package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.RiskCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class LinearProduct implements Trade {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private MarketValuation marketValuation;
    private int openRisk;
    private int intradayRisk;

    private Consumer<Integer> updateCallback;

    private final TradeAction action;
    private final LocalDate today;
    private final LocalDate previous;
    private final RiskCalculator riskCalculator;

    private static DateTimeFormatter ddMMMyy = DateTimeFormatter.ofPattern("dd-MMM-yy");

    public LinearProduct(MarketValuation marketValuation, TradeAction action, LocalDate today, LocalDate previous, RiskCalculator riskCalculator) {
        this.marketValuation = marketValuation;
        this.action = action;
        this.today = today;
        this.previous = previous;
        this.riskCalculator = riskCalculator;
    }

    @Override
    public void updateTrade(Trade updatedTrade) {
        int originalRisk = getOpenRisk() + getIntradayRisk();
        try {
            switch (updatedTrade.getAction()) {
                case New:
                    setOpenRisk(0);
                    setIntradayRisk(calculateRisk(this, today));
                    originalRisk = 0;
                    break;
                case LateBooked:
                    setOpenRisk(calculateRisk(updatedTrade, previous));
                    break;
                case EarlyBooked:
                    // do nothing
                    break;
                case Cancel:
                    setIntradayRisk(-1 * openRisk);
                    break;
                case Amend:
                    setIntradayRisk(calculateRisk(updatedTrade, today) - getOpenRisk());
                    break;
                case Reset:
                    setOpenRisk(calculateRisk(updatedTrade, today));
                    break;
            }
        } catch (RiskCalculationException rce) {
            logger.warn("Error calculating risk.", rce);
        } finally {
            int difference = originalRisk - (getOpenRisk() + getIntradayRisk());
            updateCallback.accept(difference);
        }
    }

    @Override
    public void registerUpdateCallback(Consumer<Integer> callback) {
        this.updateCallback = callback;
    }

    @Override
    public void updateMarketValuation(MarketValuation newValuation) {
        marketValuation = newValuation;
        try {
            setIntradayRisk(calculateRisk(this, today));
        } catch (RiskCalculationException rce) {
            logger.warn("Error calculating risk.", rce);
        }
    }

    protected int calculateRisk(Trade trade, LocalDate asOf) throws RiskCalculationException {
        if (asOf.equals(getToday())) {
            return riskCalculator.calculateRisk(this, getToday(), marketValuation);
        } else if (asOf.equals(getPrevious())) {
            return riskCalculator.calculateRisk(this, getPrevious(), marketValuation);
        }

        // unknown asOf
        throw new RiskCalculationException(String.format("Ignoring request to calculate risk as of %s", ddMMMyy.format(asOf)),
                this);
    }

    @Override
    public TradeAction getAction() {
        return action;
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
    public MarketValuation getMarketValuation() {
        return marketValuation;
    }

    protected void setIntradayRisk(int intradayRisk) {
        this.intradayRisk = intradayRisk;
    }

    protected void setOpenRisk(int openRisk) {
        this.openRisk = openRisk;
    }

    protected LocalDate getToday() {
        return today;
    }

    protected LocalDate getPrevious() {
        return previous;
    }

    protected RiskCalculator getRiskCalculator() {
        return riskCalculator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinearProduct that = (LinearProduct) o;
        return Objects.equals(marketValuation, that.marketValuation) &&
                action == that.action &&
                Objects.equals(today, that.today) &&
                Objects.equals(previous, that.previous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marketValuation, action, today, previous);
    }
}
