package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Trade;
import com.jasonparrott.aggregateexposure.model.SwapTrade;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class FenwickTreeTest {


    @Test
    public void testCreateTree() {
        Trade[] trades = new Trade[] {
                new SwapTrade(1, new MarketValuation(10)),
        };
        FenwickTree tree = new FenwickTree(trades);
        assertThat(tree, is(not(nullValue())));
    }

    @Test
    public void testInitialSum() throws InterruptedException {
        Trade[] trades = new Trade[] {
                new SwapTrade(1, new MarketValuation(10), 10),
                new SwapTrade(1, new MarketValuation(10), 10),
                new SwapTrade(1, new MarketValuation(10), 10),
                new SwapTrade(1, new MarketValuation(10), 10),
        };
        FenwickTree tree = new FenwickTree(trades);
        assertThat(tree.sum(3), is(40));
    }

    @Test
    public void testUpdateSumPositive() throws InterruptedException {
        Trade[] trades = new Trade[] {
                new SwapTrade(1, new MarketValuation(10), 10),
                new SwapTrade(1, new MarketValuation(10), 10),
                new SwapTrade(1, new MarketValuation(10), 10),
                new SwapTrade(1, new MarketValuation(10), 10),
        };

        FenwickTree tree = new FenwickTree(trades);

        for(int i = 0; i < trades.length; ++i) {
            int finalI = i;
            trades[i].registerUpdateCallback((diff)->{
                try {
                    tree.update(finalI, diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }

        trades[2].setExposure(30);
        assertThat(tree.sum(3), is(60));
    }

    @Test
    public void testUpdateSumNegative() throws InterruptedException {
        Trade[] trades = new Trade[] {
                new SwapTrade(1, new MarketValuation(10), 10),
                new SwapTrade(1, new MarketValuation(10), 10),
                new SwapTrade(1, new MarketValuation(10), 10),
                new SwapTrade(1, new MarketValuation(10), 10),
        };

        FenwickTree tree = new FenwickTree(trades);

        for(int i = 0; i < trades.length; ++i) {
            int finalI = i;
            trades[i].registerUpdateCallback((diff)->{
                try {
                    tree.update(finalI, diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }

        trades[1].setExposure(5);
        assertThat(tree.sum(3), is(35));
    }
}