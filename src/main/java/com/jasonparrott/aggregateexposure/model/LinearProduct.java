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
    private static DateTimeFormatter ddMMMyy = DateTimeFormatter.ofPattern("dd-MMM-yy");
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final LocalDate today;
    private final LocalDate previous;
    private final RiskCalculator riskCalculator;
    private MarketValuation marketValuation;
    private int openRisk;
    private int intradayRisk;
    private TradeAction action;
    private Consumer<Integer> updateCallback;

    public LinearProduct(MarketValuation marketValuation, TradeAction action, LocalDate today, LocalDate previous, RiskCalculator riskCalculator) throws RiskCalculationException {
        this.marketValuation = marketValuation;
        this.action = action;
        this.today = today;
        this.previous = previous;
        this.riskCalculator = riskCalculator;
        setOpenRisk(calculateRisk(previous));
    }

    @Override
    public void updateTradeAction(TradeAction update) {
        action = update;
        int originalRisk = getOpenRisk() + getIntradayRisk();
        try {
            switch (update) {
                case New:
                    setIntradayRisk(calculateRisk(today));
                    originalRisk = 0;
                    setOpenRisk(0);
                    break;
                case LateBooked:
                    setOpenRisk(calculateRisk(previous));
                    break;
                case EarlyBooked:
                    return;
                case Cancel:
                    setIntradayRisk(-1 * openRisk);
                    break;
                case Amend:
                    setIntradayRisk(calculateRisk(today) - getOpenRisk());
                    break;
                case Reset:
                    setOpenRisk(calculateRisk(today));
                    break;
            }

            int difference = (getOpenRisk() + getIntradayRisk()) - originalRisk;
            updateCallback.accept(difference);
        } catch (RiskCalculationException rce) {
            logger.warn("Error calculating risk.", rce);
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
            int originalIntradayRisk = getIntradayRisk();
            setIntradayRisk(calculateRisk(today));
            int difference = getIntradayRisk() - originalIntradayRisk;
            updateCallback.accept(difference);
        } catch (RiskCalculationException rce) {
            logger.warn("Error calculating risk.", rce);
        }
    }

    protected int calculateRisk(LocalDate asOf) throws RiskCalculationException {
        if (asOf.equals(getToday())) {
            return riskCalculator.calculateRisk(this, asOf, marketValuation);
        } else if (asOf.equals(getPrevious())) {
            return riskCalculator.calculateRisk(this, asOf, marketValuation);
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
    public MarketValuation getMarketValuation() {
        return marketValuation;
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
