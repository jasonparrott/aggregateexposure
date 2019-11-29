package com.jasonparrott.aggregateexposure.calculators.product;

public class PvCalculator {

    /*
    switch (trades.getAction()) {
            case New:
                intraday = calculateRiskAsOf(today);
                origionalRisk = 0d;
                open = 0d;
                break;
            case LateBooked:
                open = calculateRiskAsOf(previous);
                break;
            case EarlyBooked:
                return trade.getMetrics(); // nothing changes
            case Cancel:
                intraday = -1 * trade.getMetrics().getOpenRisk();
                break;
            case Amend:
                intraday = calculateRiskAsOf(today) - trade.getMetrics().getOpenRisk();
                break;
            case Reset:
                open = calculateRiskAsOf(today);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + trade.getAction());
        }

        double change = (open + intraday) - origionalRisk;
     */
}
