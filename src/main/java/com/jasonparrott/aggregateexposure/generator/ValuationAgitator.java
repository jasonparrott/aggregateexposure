package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskEngine;
import com.jasonparrott.aggregateexposure.model.MarketValuation;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class ValuationAgitator {
    private final int iterations;
    private final MarketValuationEngine marketValuationEngine;
    private final RiskEngine riskEngine;
    private boolean shouldRun = false;

    public ValuationAgitator(int iterations, MarketValuationEngine marketValuationEngine, RiskEngine riskEngine) {
        this.iterations = iterations;
        this.marketValuationEngine = marketValuationEngine;
        this.riskEngine = riskEngine;
    }

    public void start() {
        shouldRun = true;
        long start = System.nanoTime();
        AtomicLong updateCount = new AtomicLong(0L);
        for (int i = 0; (i < iterations) && shouldRun; ++i) {
            MarketValuation valuation = marketValuationEngine.getRandomValulation();
            valuation.update(50d + (100d * Math.random()));
            riskEngine.updateValuation(valuation);
            updateCount.getAndIncrement();
        }
        long end = System.nanoTime();

        System.out.println(String.format("Updated %d valuations in %d ms.",
                updateCount.longValue(), Duration.ofNanos(end - start).toMillis()));
    }

    public void stop() {
        shouldRun = false;
    }
}
