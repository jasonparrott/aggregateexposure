package com.jasonparrott.aggregateexposure.perf;

import com.jasonparrott.aggregateexposure.OneShotTradeListener;
import com.jasonparrott.aggregateexposure.RiskEngine;
import com.jasonparrott.aggregateexposure.TradeListener;
import com.jasonparrott.aggregateexposure.generator.MarketValuationEngine;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openjdk.jmh.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = {PerfTestConfiguration.class})
@RunWith(SpringRunner.class)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ValuationPerfIT extends BasePerformanceTest {

    private static RiskEngine riskEngine;
    private static MarketValuationEngine valuationEngine;
    private static TradeListener tradeListener;

    private AtomicInteger count = new AtomicInteger(0);
    private ArrayList<MarketValuation> valuations;

    @Autowired
    void setTradeListener(TradeListener tradeListener) {
        ValuationPerfIT.tradeListener = tradeListener;
    }

    @Autowired
    void setRiskEngine(RiskEngine riskEngine) {
        ValuationPerfIT.riskEngine = riskEngine;
    }

    @Autowired
    void setValuationEngine(MarketValuationEngine valuationEngine) {
        ValuationPerfIT.valuationEngine = valuationEngine;
    }

    @Setup(Level.Trial)
    public void setup() throws InterruptedException, ExecutionException {
        valuations = new ArrayList<>(valuationEngine.getAllValuations());
    }

    @Before
    public void testSetup() throws InterruptedException, ExecutionException {
        valuations = new ArrayList<>(valuationEngine.getAllValuations());
        while (riskEngine.getTradeCount() < ((OneShotTradeListener) tradeListener).getTradeSetSize()) {
            Thread.sleep(100);
        }
        for (MarketValuation valuation : valuations) {
            riskEngine.updateValuation(valuation).get();
        }
        System.out.println("Setup complete.");
    }

    @Benchmark
    public void testValuations() throws ExecutionException, InterruptedException {
        MarketValuation val = valuations.get(count.getAndIncrement() % 8);
        val.update(Math.random());
        riskEngine.updateValuation(val).get();
    }
}
