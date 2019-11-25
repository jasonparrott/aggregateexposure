package com.jasonparrott.aggregateexposure.calculators;

import com.jasonparrott.aggregateexposure.RiskCalculator;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.time.LocalDate;
import java.util.Random;

public class SwapCalculator implements RiskCalculator {
    private final Random r = new Random();
    @Override
    public int calculateRisk(Trade trade, LocalDate asOf, MarketValuation valuation) {
        return r.nextInt(2000);
    }
}
