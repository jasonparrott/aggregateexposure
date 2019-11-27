package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Random;

public class SwapMetricsCalculator implements MetricsCalculator {
    private final Random r = new Random();
    private final Collection<Calculator> inputs;

    public SwapMetricsCalculator(Collection<Calculator> inputs) {
        this.inputs = inputs;
    }

    @Override
    public ProductMetrics calculateRisk(Trade trade, LocalDate today, LocalDate previous) {
        //double intrday = ;

        double origionalRisk = trade.getMetrics().getOpenRisk() + trade.getMetrics().getIntradayRisk();
        double intraday = 0;
        double open = 0;

        switch (trade.getAction()) {
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
                intraday = trade.getMetrics().getOpenRisk() * -1;
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

        return new DefaultProductMetrics(open, intraday, change);
    }


    private double calculateRiskAsOf(LocalDate today) {
        return inputs.stream().mapToDouble(i -> i.getCalculationResult().getResult()).sum() * Math.random();
    }
}