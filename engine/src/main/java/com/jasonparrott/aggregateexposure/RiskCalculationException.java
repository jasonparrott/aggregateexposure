package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.trade.Trade;

public class RiskCalculationException extends Exception {

    private final Trade trade;

    public RiskCalculationException(Trade trade) {
        this.trade = trade;
    }

    public RiskCalculationException(String message, Trade trade) {
        super(message);
        this.trade = trade;
    }

    public RiskCalculationException(String message, Throwable cause, Trade trade) {
        super(message, cause);
        this.trade = trade;
    }

    public RiskCalculationException(Throwable cause, Trade trade) {
        super(cause);
        this.trade = trade;
    }

    public RiskCalculationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Trade trade) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.trade = trade;
    }

    public Trade getTrade() {
        return trade;
    }
}
