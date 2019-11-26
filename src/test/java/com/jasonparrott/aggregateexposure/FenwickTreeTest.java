package com.jasonparrott.aggregateexposure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

public class FenwickTreeTest {
    private static LocalDate TODAY = LocalDate.of(2019, 11, 13);
    private static LocalDate PREVIOUS = LocalDate.of(2019, 11, 12);
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);
    @Mock
    private MarketValuation valuation;
    @Mock
    private RiskCalculator calculator;

    @Mock
    private Calculator c1;
    @Mock
    private Calculator c2;
    @Mock
    private Calculator c3;

    private Collection<Calculator> inputs = ImmutableList.of(c1, c2);
    private Multimap<Calculator, Calculator> interestSet = MultimapBuilder.hashKeys().linkedHashSetValues().build();

    @Before
    public void setup() {
        interestSet.put(c2, c3);
    }

    @Test
    public void testCreateTree() throws RiskCalculationException {
        Trade[] trades = new Trade[]{
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0)
        };
        FenwickTree tree = new FenwickTree(trades);
        assertThat(tree, is(not(nullValue())));
    }

    @Test
    public void testInitialSum() throws InterruptedException, RiskCalculationException {
        doReturn(10).when(calculator).calculateRisk(any(Trade.class), eq(PREVIOUS), any(Collection.class));
        Trade[] trades = new Trade[]{
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0),
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0),
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0),
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0)
        };
        FenwickTree tree = new FenwickTree(trades);
        assertThat(tree.sum(3), is(40));
    }

    @Test
    public void testUpdateSumPositive() throws InterruptedException, RiskCalculationException {
        doReturn(10).when(calculator).calculateRisk(any(Trade.class), eq(PREVIOUS), any(Collection.class));
        Trade[] trades = new Trade[]{
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0),
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0),
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0),
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0)
        };

        FenwickTree tree = new FenwickTree(trades);

        for (int i = 0; i < trades.length; ++i) {
            int finalI = i;
            trades[i].registerUpdateCallback((diff) -> {
                try {
                    tree.update(finalI, diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }

        assertThat(tree.sum(3), is(40));
        doReturn(15).when(calculator).calculateRisk(any(Trade.class), eq(TODAY), any(Collection.class));

        trades[2].updateTradeAction(TradeAction.Amend);
        assertThat(tree.sum(3), is(45));
    }

    @Test
    public void testUpdateSumNegative() throws InterruptedException, RiskCalculationException {
        doReturn(10).when(calculator).calculateRisk(any(Trade.class), eq(PREVIOUS), any(Collection.class));
        Trade[] trades = new Trade[]{
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0),
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0),
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0),
                new SwapTrade(inputs, interestSet, TradeAction.New, TODAY, PREVIOUS, calculator, 0)
        };

        FenwickTree tree = new FenwickTree(trades);

        for (int i = 0; i < trades.length; ++i) {
            int finalI = i;
            trades[i].registerUpdateCallback((diff) -> {
                try {
                    tree.update(finalI, diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }

        assertThat(tree.sum(3), is(40));
        doReturn(5).when(calculator).calculateRisk(any(Trade.class), eq(TODAY), any(Collection.class));
        trades[2].updateTradeAction(TradeAction.Amend);
        assertThat(tree.sum(3), is(35));
    }
}