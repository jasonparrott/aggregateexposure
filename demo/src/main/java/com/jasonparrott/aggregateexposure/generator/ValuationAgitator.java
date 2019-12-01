package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskEngine;
import com.jasonparrott.aggregateexposure.model.MarketValuation;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class ValuationAgitator {
    private final int iterations;
    private final MarketValuationEngine marketValuationEngine;
    private final ExecutorService executorService;

    private final RiskEngine riskEngine;
    private boolean shouldRun = false;

    public ValuationAgitator(int iterations, MarketValuationEngine marketValuationEngine, ExecutorService executorService, RiskEngine riskEngine) {
        this.iterations = iterations;
        this.marketValuationEngine = marketValuationEngine;
        this.executorService = executorService;
        this.riskEngine = riskEngine;
    }

    public void start() throws InterruptedException {
        shouldRun = true;
        List<Future> futures = new LinkedList<>();
        long start = System.nanoTime();
        AtomicLong updateCount = new AtomicLong(0L);
        for (int i = 0; (i < iterations) && shouldRun; ++i) {
            MarketValuation valuation = marketValuationEngine.getRandomValulation();
            valuation.update(50d + (100d * Math.random()));
            futures.add(riskEngine.updateValuation(valuation));
            updateCount.getAndIncrement();
        }

        while (futures.stream().anyMatch(f -> !f.isDone()))
            Thread.sleep(50);

        long end = System.nanoTime();

        System.out.println(String.format("Updated %d valuations in %d ms.",
                updateCount.longValue(), Duration.ofNanos(end - start).toMillis()));
    }

    public void stop() {
        shouldRun = false;
    }
}
