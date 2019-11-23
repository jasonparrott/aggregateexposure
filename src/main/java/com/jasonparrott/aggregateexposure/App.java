package com.jasonparrott.aggregateexposure;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jasonparrott.aggregateexposure.generator.BondGenerator;
import com.jasonparrott.aggregateexposure.generator.OptionGenerator;
import com.jasonparrott.aggregateexposure.generator.PositionGenerator;
import com.jasonparrott.aggregateexposure.generator.SwapGenerator;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Position;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class App {
//    private static int VALUATIONS = 10000;
//    private static int CLIENTS = 200;
//    private static int MIN_POSITIONS_PER_ASSETCLASS = 500;
//    private static int MAX_POSITIONS_PER_ASSETCLASS = 5000;
    private static Random r = new Random();

    private static Multimap<MarketValuation, Position> positionMultiMap = MultimapBuilder
                                                                            .hashKeys() // we have a well defined hash
                                                                            .linkedHashSetValues(3 * 2250) // 3 products * mid per asset class
                                                                            .build();

    public static void main(String... args) {

        int valuations = Integer.parseInt(args[0]);
        int clients = Integer.parseInt(args[1]);
        int minPerAssetClass = Integer.parseInt(args[2]);
        int maxPerAssetClass = Integer.parseInt(args[3]);

        SwapGenerator swapGenerator = new SwapGenerator();
        BondGenerator bondGenerator = new BondGenerator();
        OptionGenerator optionGenerator = new OptionGenerator();

        MarketValuationEngine marketValuationEngine = new MarketValuationEngine(valuations);
        // build the modes
        long positionCount = 0L;
        for(int i = 0; i < clients; ++i) {
            List<Position> positions = new LinkedList<>();
            Client client = new Client(i);
            positions.addAll(createPositions(client, swapGenerator, marketValuationEngine, minPerAssetClass, maxPerAssetClass));
            positions.addAll(createPositions(client, bondGenerator, marketValuationEngine, minPerAssetClass, maxPerAssetClass));
            positions.addAll(createPositions(client, optionGenerator, marketValuationEngine, minPerAssetClass, maxPerAssetClass));
            positionCount += positions.size();
            client.setPositions(positions.toArray(new Position[positions.size()]));
        }

        System.out.println(String.format("Created collection of %d clients using %d positions in %d market valuations",
                               clients, positionCount, valuations));

        ExecutorService executor = Executors.newWorkStealingPool();

        // begin updates by picking a random market valuation
        long start = System.nanoTime();
        final AtomicLong updateCount = new AtomicLong(0L);
        for(int i = 0; i < 10000; ++i) {
            MarketValuation valuation = marketValuationEngine.getRandomValulation();
            valuation.update(r.nextInt(200)); // somewhere between 0 and 200
            for (final Position position : positionMultiMap.get(valuation)) {
                executor.submit(() -> {
                    position.setExposure((valuation.getValue())); // set exposure to some portion of valuation as we're not actually computing it
                    updateCount.getAndIncrement();
                });
            }
        }
        long end = System.nanoTime();
        Duration duration = Duration.ofNanos(end - start);
        System.out.println(String.format("%d updates took: %d ms", updateCount.longValue(), duration.toMillis() ));
    }

    private static Collection<Position> createPositions(Client client,
                                                        PositionGenerator generator,
                                                        MarketValuationEngine marketValuationEngine,
                                                        int minPerAssetClass,
                                                        int maxPerAssetClass) {
        int count = minPerAssetClass + r.nextInt(maxPerAssetClass - minPerAssetClass);
        LinkedList<Position> positions = new LinkedList<>();
        for(int i = 0; i < count; ++i) {
            Position position = generator.createPosition(client, marketValuationEngine.getRandomValulation());
            positions.add(position);
            positionMultiMap.put(position.getMarketValuation(), position);
        }
        return positions;
    }


}
