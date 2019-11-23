package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.MarketValuation;
import com.jasonparrott.aggregateexposure.model.Position;
import com.jasonparrott.aggregateexposure.model.SwapPosition;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class FenwickTreeTest {


    @Test
    public void testCreateTree() {
        Position[] positions = new Position[] {
                new SwapPosition(1, new MarketValuation(10)),
        };
        FenwickTree tree = new FenwickTree(positions);
        assertThat(tree, is(not(nullValue())));
    }

    @Test
    public void testInitialSum() throws InterruptedException {
        Position[] positions = new Position[] {
                new SwapPosition(1, new MarketValuation(10), 10),
                new SwapPosition(1, new MarketValuation(10), 10),
                new SwapPosition(1, new MarketValuation(10), 10),
                new SwapPosition(1, new MarketValuation(10), 10),
        };
        FenwickTree tree = new FenwickTree(positions);
        assertThat(tree.sum(3), is(40));
    }

    @Test
    public void testUpdateSumPositive() throws InterruptedException {
        Position[] positions = new Position[] {
                new SwapPosition(1, new MarketValuation(10), 10),
                new SwapPosition(1, new MarketValuation(10), 10),
                new SwapPosition(1, new MarketValuation(10), 10),
                new SwapPosition(1, new MarketValuation(10), 10),
        };

        FenwickTree tree = new FenwickTree(positions);

        for(int i = 0; i < positions.length; ++i) {
            int finalI = i;
            positions[i].registerUpdateCallback((diff)->{
                try {
                    tree.update(finalI, diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }

        positions[2].setExposure(30);
        assertThat(tree.sum(3), is(60));
    }

    @Test
    public void testUpdateSumNegative() throws InterruptedException {
        Position[] positions = new Position[] {
                new SwapPosition(1, new MarketValuation(10), 10),
                new SwapPosition(1, new MarketValuation(10), 10),
                new SwapPosition(1, new MarketValuation(10), 10),
                new SwapPosition(1, new MarketValuation(10), 10),
        };

        FenwickTree tree = new FenwickTree(positions);

        for(int i = 0; i < positions.length; ++i) {
            int finalI = i;
            positions[i].registerUpdateCallback((diff)->{
                try {
                    tree.update(finalI, diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }

        positions[1].setExposure(5);
        assertThat(tree.sum(3), is(35));
    }
}