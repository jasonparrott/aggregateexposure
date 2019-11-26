package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.generator.FakePortfolioGenerator;
import com.jasonparrott.aggregateexposure.generator.RiskGraphGenerator;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Trade;
import com.jasonparrott.aggregateexposure.model.TradeAction;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class App {

    private static Random r = new Random();
    private static PortfolioBuilder portfolioBuilder;

//    private static Multimap<MarketValuation, Trade> positionMultiMap = MultimapBuilder
//            .hashKeys() // we have a well defined hash
//            .linkedHashSetValues(3 * 2250) // 3 products * mid per asset class
//            .build();

    public static void main(String... args) throws RiskCalculationException {

        int valuations = Integer.parseInt(args[0]);
        int clients = Integer.parseInt(args[1]);
        int minPerAssetClass = Integer.parseInt(args[2]);
        int maxPerAssetClass = Integer.parseInt(args[3]);

        MarketValuationEngine marketValuationEngine = new MarketValuationEngine(valuations);
        Graph<Calculator, DefaultEdge> riskGraph = RiskGraphGenerator.generateGraph(marketValuationEngine.getAllValuations(), 0.25d);

        System.out.println("Finding graph roots.");
        List<Calculator> roots = new LinkedList<>();
        for(Calculator c : riskGraph.vertexSet()) {
            if (riskGraph.inDegreeOf(c) == 0)
                roots.add(c);
        }

        PortfolioBuilder portfolioBuilder = new FakePortfolioGenerator(riskGraph, roots, minPerAssetClass, maxPerAssetClass);
        long positionsCount = 0L;
        List<Trade> updateableTrades = new ArrayList<Trade>();
        // build the trade set
        for (int i = 0; i < clients; ++i) {
            Client client = new Client(i);
            List<Trade> trades = portfolioBuilder.getPortfolio(client);
            // pick a few randomly to be potential update targets
            for(int j = 0; j < 20; ++j) {
                updateableTrades.add(trades.get(r.nextInt(trades.size())));
            }
            positionsCount += trades.size();
            client.setTrades(trades.toArray(new Trade[trades.size()]));
            if (i % 50 == 0)
                System.out.println(String.format("%d of %d complete.", i, clients));
        }

        System.out.println(String.format("Created collection of %d clients using %d positions in %d market valuations",
                clients, positionsCount, valuations));

        ExecutorService executor = Executors.newWorkStealingPool();

        // begin updates by picking a random market valuation
        long start = System.nanoTime();
        final AtomicLong updateCount = new AtomicLong(0L);
        for (int i = 0; i < 10000; ++i) {
            // either a trade update or a market valuation update
            if (updateCount.get() % 5 == 0) {
                // pick a few randomly
                long nanotime = System.nanoTime();
                updateableTrades.stream().filter(t -> nanotime % 4 == 0).forEach(t -> {
                    TradeAction nextAction = findNextAction(t.getAction(), nanotime);
                    if (nextAction != null)
                        t.updateTradeAction(nextAction);
                    updateCount.getAndIncrement();
                });
            } else {
                // marketData update
                MarketValuation valuation = marketValuationEngine.getRandomValulation();
                valuation.update(r.nextInt(200)); // somewhere between 0 and 200
            }
        }
        long end = System.nanoTime();
        Duration duration = Duration.ofNanos(end - start);
        System.out.println(String.format("%d updates took: %d ms", updateCount.longValue(), duration.toMillis()));
    }

    private static TradeAction findNextAction(TradeAction action, long nanoTime) {
        if (action == null)
            return TradeAction.Amend;

        switch (action) {
            case Cancel:
                return null; // no further actions
            default: {
                int result = (int) (nanoTime % 9);
                switch (result) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        return TradeAction.Amend;
                    case 4:
                        return TradeAction.LateBooked;
                    case 5:
                        return TradeAction.EarlyBooked;
                    case 6:
                    case 7:
                        return TradeAction.Reset;
                    case 8:
                        return TradeAction.Cancel;
                    default:
                        return TradeAction.Amend;
                }
            }
        }
    }


}
