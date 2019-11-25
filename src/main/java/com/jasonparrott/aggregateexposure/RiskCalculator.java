package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.time.LocalDate;

public interface RiskCalculator {
    int calculateRisk(Trade trade, LocalDate asOf, MarketValuation valuation);
}
