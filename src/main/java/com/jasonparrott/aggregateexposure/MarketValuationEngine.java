package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.MarketValuation;

import java.util.Random;

public class MarketValuationEngine {

    private final MarketValuation[] valuations;
    private final Random r = new Random();

    public MarketValuationEngine(int numberOfValuations) {
        valuations = new MarketValuation[numberOfValuations];
        for(int i = 0; i < numberOfValuations; ++i) {
            valuations[i] = new MarketValuation(r.nextInt(200));
        }
    }

    public MarketValuation getRandomValulation() {
        return valuations[r.nextInt(valuations.length)];
    }
}
