package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.calculators.product.MetricsCalculator;
import com.jasonparrott.aggregateexposure.calculators.product.ProductMetrics;
import com.jasonparrott.aggregateexposure.model.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

public class FenwickTreeTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private static LocalDate TODAY = LocalDate.of(2019, 11, 13);

    private static LocalDate PREVIOUS = LocalDate.of(2019, 11, 12);

    @Mock
    private MarketValuation valuation;

    @Mock
    private MetricsCalculator calculator;

    @Mock
    private ProductMetrics metrics;

    @Before
    public void setup() {

    }

    @Test
    public void testCreateTree() throws RiskCalculationException {
        SecurityGroup[] trades = new SecurityGroup[]{
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator)
        };
        FenwickTree tree = new FenwickTree(trades);
        assertThat(tree, is(not(nullValue())));
    }

    @Test
    public void testInitialSum() throws InterruptedException, RiskCalculationException {
        doReturn(10d).when(metrics).getOpenRisk();
        doReturn(metrics).when(calculator).calculateRisk(any(SecurityGroup.class), eq(TODAY), eq(PREVIOUS));

        SecurityGroup[] trades = new SecurityGroup[]{
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator),
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator),
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator),
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator)
        };
        Arrays.stream(trades).forEach(s -> s.updateMetrics().run());

        FenwickTree tree = new FenwickTree(trades);
        assertThat(tree.sum(3), is(40));
    }

    @Test
    public void testUpdateSumPositive() throws InterruptedException, RiskCalculationException {
        doReturn(10d).when(metrics).getOpenRisk();
        doReturn(metrics).when(calculator).calculateRisk(any(SecurityGroup.class), eq(TODAY), eq(PREVIOUS));

        SecurityGroup[] trades = new SecurityGroup[]{
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator),
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator),
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator),
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator)
        };
        Arrays.stream(trades).forEach(s -> s.updateMetrics().run());

        FenwickTree tree = new FenwickTree(trades);

        for (int i = 0; i < trades.length; ++i) {
            int finalI = i;
            trades[i].registerUpdateCallback((diff) -> {
                try {
                    tree.update(finalI, (Double) diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }

        assertThat(tree.sum(3), is(40));
        doReturn(5d).when(metrics).getIntradayChange();
        Arrays.stream(trades).forEach(s -> s.updateMetrics().run());
        assertThat(tree.sum(3), is(60)); // added 5 to the metrics which is the same for all
    }

    @Test
    public void testUpdateSumNegative() throws InterruptedException, RiskCalculationException {
        doReturn(10d).when(metrics).getOpenRisk();
        doReturn(metrics).when(calculator).calculateRisk(any(SecurityGroup.class), eq(TODAY), eq(PREVIOUS));

        SecurityGroup[] trades = new SecurityGroup[]{
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator),
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator),
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator),
                new LinearSecurityGroup(0, ProductType.Bond, TODAY, PREVIOUS, calculator)
        };
        Arrays.stream(trades).forEach(s -> s.updateMetrics().run());
        FenwickTree tree = new FenwickTree(trades);

        for (int i = 0; i < trades.length; ++i) {
            int finalI = i;
            trades[i].registerUpdateCallback((diff) -> {
                try {
                    tree.update(finalI, (Double) diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }

        assertThat(tree.sum(3), is(40));
        doReturn(-5d).when(metrics).getIntradayChange();
        Arrays.stream(trades).forEach(s -> s.updateMetrics().run());
        assertThat(tree.sum(3), is(20));
    }
}