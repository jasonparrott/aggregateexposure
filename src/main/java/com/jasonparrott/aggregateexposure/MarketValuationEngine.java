package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.MarketValuation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class MarketValuationEngine {

    private final ArrayList<MarketValuation> valuations;
    private final Random r = new Random();

    public MarketValuationEngine(int numberOfValuations) {
        valuations = new ArrayList<>(numberOfValuations);
        for (int i = 0; i < numberOfValuations; ++i) {
            valuations.add(new MarketValuation(r.nextInt(200)));
        }
    }

    public Collection<MarketValuation> getAllValuations() {
        return valuations;
    }

    public MarketValuation getRandomValulation() {
        return valuations.get(r.nextInt(valuations.size()));
    }
}
