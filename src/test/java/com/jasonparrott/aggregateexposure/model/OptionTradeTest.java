package com.jasonparrott.aggregateexposure.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.calculators.product.OptionCalculator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OptionTradeTest {
    private static LocalDate TODAY = LocalDate.of(2019, 11, 13);
    private static LocalDate PREVIOUS = LocalDate.of(2019, 11, 12);
    private static LocalDate FUTURE = LocalDate.of(2019, 11, 20);
    private final int OPEN = 200;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);
    @Mock
    private MarketValuation marketValuation;
    @Mock
    private OptionCalculator riskCalculator;
    @Mock
    private Consumer<Integer> updateCallback;
    private OptionTrade product;

    @Mock
    private Calculator c1;
    @Mock
    private Calculator c2;
    @Mock
    private Calculator c3;

    private List<Calculator> inputs = ImmutableList.of(c1, c2);
    private Multimap<Calculator, Calculator> interestSet = MultimapBuilder.hashKeys().linkedHashSetValues().build();

    @Before
    public void setup() throws RiskCalculationException {
        interestSet.put(c2, c3);
        product = new OptionTrade(inputs, interestSet, TradeAction.New, riskCalculator, 0);
        product.registerUpdateCallback(updateCallback);
        reset(riskCalculator);
    }

    @Test
    public void updateTradeActionNew() {
        final int INTRADAY = 340;
        doReturn(INTRADAY).when(riskCalculator).calculateRisk(product, LocalDate.now(), inputs);
        product.updateTradeAction(TradeAction.New);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(INTRADAY));
        verify(updateCallback, times(1)).accept(INTRADAY);
    }

    @Test
    public void updateTradeActionLateBooking() {
        final int INTRADAY = 340;
        doReturn(INTRADAY).when(riskCalculator).calculateRisk(product, LocalDate.now(), inputs);
        product.updateTradeAction(TradeAction.LateBooked);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(INTRADAY));
        verify(updateCallback, times(1)).accept(INTRADAY);
    }

    @Test
    public void updateTradeActionEarlyBooking() {
        reset(riskCalculator); // remove setup call
        product.updateTradeAction(TradeAction.EarlyBooked);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(0));
        verify(updateCallback, never()).accept(0);
        verify(riskCalculator, never()).calculateRisk(eq(product), any(LocalDate.class), eq(inputs));
    }

    @Test
    public void updateTradeActionCancel() {
        final int CURRENT = 340;
        doReturn(CURRENT).when(riskCalculator).calculateRisk(product, LocalDate.now(), inputs);

        product.updateTradeAction(TradeAction.Amend);
        assertThat(product.getIntradayRisk(), is(CURRENT));
        reset(updateCallback);
        reset(riskCalculator);

        product.updateTradeAction(TradeAction.Cancel);
        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(-1 * CURRENT));
        verify(updateCallback, times(1)).accept(-1 * CURRENT);
        verify(riskCalculator, never()).calculateRisk(eq(product), any(LocalDate.class), eq(inputs)); // no op, just flip OPEN
    }

    @Test
    public void updateTradeActionAmend() {
        final int INTRADAY = 340;
        doReturn(INTRADAY).when(riskCalculator).calculateRisk(product, LocalDate.now(), inputs);
        product.updateTradeAction(TradeAction.Amend);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(INTRADAY));
        verify(updateCallback, times(1)).accept(INTRADAY);
    }

    @Test
    public void updateTradeActionReset() {
        final int INTRADAY = 340;
        doReturn(INTRADAY).when(riskCalculator).calculateRisk(product, LocalDate.now(), inputs);
        product.updateTradeAction(TradeAction.Reset);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(INTRADAY));
        verify(updateCallback, times(1)).accept(INTRADAY);
    }

    @Test
    public void updateTradeActionWithExceptionThrown() {
        doThrow(RiskCalculationException.class).when(riskCalculator).calculateRisk(product, LocalDate.now(), inputs);
        product.updateTradeAction(TradeAction.Amend);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(0));
        verify(updateCallback, never()).accept(anyInt());
    }
    
    @Test
    public void calculateRiskAsOfToday() throws RiskCalculationException {
        product.calculateRisk();
        verify(riskCalculator, never()).calculateRisk(product, PREVIOUS, inputs);
        verify(riskCalculator, times(1)).calculateRisk(product, LocalDate.now(), inputs);
    }

    @Test
    public void calculateRiskAsOfPrevious() throws RiskCalculationException {
        product.calculateRisk();
        verify(riskCalculator, never()).calculateRisk(product, TODAY, inputs);
        verify(riskCalculator, times(1)).calculateRisk(product, LocalDate.now(), inputs);
    }
}
