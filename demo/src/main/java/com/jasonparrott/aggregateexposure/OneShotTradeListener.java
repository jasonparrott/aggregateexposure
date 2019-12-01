package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.CreditRating;
import com.jasonparrott.aggregateexposure.model.trade.Trade;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Injects a single batch of trades into the engine at once.
 */
public class OneShotTradeListener implements TradeListener {
    private final List<Trade> tradeSet = new LinkedList<>();
    private final Random r = new Random();
    private boolean hasProvided;

    public OneShotTradeListener(int clients, PortfolioBuilder portfolioBuilder) throws RiskCalculationException {
        System.out.println("Building clients and trades...");
        for (int i = 0; i < clients; ++i) {
            Client client = new Client(i, getCreditRating());
            tradeSet.addAll(portfolioBuilder.getPortfolio(client));
            if (i % 10 == 0)
                System.out.println(String.format("Completed %d of %d clients.", i, clients));
        }
    }

    public int getTradeSetSize() {
        return tradeSet.size();
    }

    @Override
    public Collection<Trade> get() {
        if (hasProvided) {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        } else {
            return tradeSet;
        }
    }

    private CreditRating getCreditRating() {
        switch (r.nextInt(7)) {
            case 0:
                return CreditRating.AAA;
            case 1:
                return CreditRating.AA;
            case 2:
                return CreditRating.A;
            case 3:
                return CreditRating.BBB;
            case 4:
                return CreditRating.BB;
            case 5:
                return CreditRating.B;
            default:
                return CreditRating.CCC;
        }
    }
}