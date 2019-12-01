package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.generator.ValuationAgitator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AggregateExposureApplication implements ApplicationRunner {
    @Autowired
    public RiskEngine riskEngine;
    @Autowired
    public ValuationAgitator agitator;
    @Autowired
    public TradeListener listener;

    public static void main(String[] args) {
        SpringApplication.run(AggregateExposureApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Waiting for engine init.");
        OneShotTradeListener tradeListener = (OneShotTradeListener) listener;
        while (riskEngine.getTradeCount() < tradeListener.getTradeSetSize()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException iae) {
                Thread.interrupted();
            }
        }
        System.out.println("Begining updates of market valuations.");
        agitator.start();
        System.out.println("Press Ctrl-C to stop app.");
        while (true) {
            Thread.sleep(1000);
        }
    }
}