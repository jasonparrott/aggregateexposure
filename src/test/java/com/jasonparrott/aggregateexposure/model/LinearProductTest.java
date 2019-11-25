package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.RiskCalculator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class LinearProductTest {
    private static LocalDate TODAY = LocalDate.of(2019, 11, 13);
    private static LocalDate PREVIOUS = LocalDate.of(2019, 11, 12);
    private static LocalDate FUTURE = LocalDate.of(2019, 11, 20);
    private final int OPEN = 200;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);
    @Mock
    private MarketValuation marketValuation;
    @Mock
    private RiskCalculator riskCalculator;
    @Mock
    private Consumer<Integer> updateCallback;
    private LinearProduct product;

    @Before
    public void setup() throws RiskCalculationException {
        product = new TestableLinearProduct(marketValuation, TradeAction.New, TODAY, PREVIOUS, riskCalculator, 0);
        product.registerUpdateCallback(updateCallback);
        reset(riskCalculator);
    }

    @Test
    public void updateTradeActionNew() {
        final int INTRADAY = 340;
        doReturn(INTRADAY).when(riskCalculator).calculateRisk(product, TODAY, marketValuation);
        product.updateTradeAction(TradeAction.New);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(INTRADAY));
        verify(updateCallback, times(1)).accept(INTRADAY);
    }

    @Test
    public void updateTradeActionLateBooking() {
        final int PREV = 340;
        doReturn(PREV).when(riskCalculator).calculateRisk(product, PREVIOUS, marketValuation);
        product.updateTradeAction(TradeAction.LateBooked);

        assertThat(product.getOpenRisk(), is(PREV));
        assertThat(product.getIntradayRisk(), is(0));
        verify(updateCallback, times(1)).accept(PREV);
    }

    @Test
    public void updateTradeActionEarlyBooking() {
        reset(riskCalculator); // remove setup call
        product.updateTradeAction(TradeAction.EarlyBooked);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(0));
        verify(updateCallback, never()).accept(anyInt());
        verify(riskCalculator, never()).calculateRisk(eq(product), any(LocalDate.class), eq(marketValuation));
    }

    @Test
    public void updateTradeActionCancel() {
        final int PREV = 340;
        doReturn(PREV).when(riskCalculator).calculateRisk(product, PREVIOUS, marketValuation);
        product.updateTradeAction(TradeAction.LateBooked); // set open to  PREV

        reset(riskCalculator);
        product.updateTradeAction(TradeAction.Cancel);
        assertThat(product.getOpenRisk(), is(PREV));
        assertThat(product.getIntradayRisk(), is(-1 * PREV));
        verify(updateCallback, times(1)).accept(-1 * PREV);
        verify(riskCalculator, never()).calculateRisk(eq(product), any(LocalDate.class), eq(marketValuation)); // no op, just flip OPEN
    }

    @Test
    public void updateTradeActionAmend() {
        final int CURRENT = 340;
        doReturn(CURRENT).when(riskCalculator).calculateRisk(product, TODAY, marketValuation);
        product.updateTradeAction(TradeAction.Amend);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(CURRENT));
        verify(updateCallback, times(1)).accept(CURRENT);
    }

    @Test
    public void updateTradeActionReset() {
        final int CURRENT = 340;
        doReturn(CURRENT).when(riskCalculator).calculateRisk(product, TODAY, marketValuation);
        product.updateTradeAction(TradeAction.Reset);

        assertThat(product.getOpenRisk(), is(CURRENT));
        assertThat(product.getIntradayRisk(), is(0));
        verify(updateCallback, times(1)).accept(CURRENT);
    }

    @Test
    public void updateTradeActionWithExceptionThrown() {
        doThrow(RiskCalculationException.class).when(riskCalculator).calculateRisk(product, TODAY, marketValuation);
        product.updateTradeAction(TradeAction.Amend);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(0));
        verify(updateCallback, never()).accept(anyInt());
    }

    @Test
    public void updateMarketValuation() {
        final int CURRENT = 286;
        doReturn(CURRENT).when(riskCalculator).calculateRisk(product, TODAY, marketValuation);
        product.updateMarketValuation(marketValuation);
        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(CURRENT));
        verify(updateCallback, times(1)).accept(CURRENT);
    }

    @Test
    public void updateMarketValuationWithExceptionThrown() {
        doThrow(RiskCalculationException.class).when(riskCalculator).calculateRisk(product, TODAY, marketValuation);
        product.updateMarketValuation(marketValuation);

        assertThat(product.getOpenRisk(), is(0));
        assertThat(product.getIntradayRisk(), is(0));
        verify(updateCallback, never()).accept(anyInt());
    }

    @Test
    public void calculateRiskAsOfToday() throws RiskCalculationException {
        product.calculateRisk(TODAY);
        verify(riskCalculator, never()).calculateRisk(product, PREVIOUS, marketValuation);
        verify(riskCalculator, times(1)).calculateRisk(product, TODAY, marketValuation);
    }

    @Test
    public void calculateRiskAsOfPrevious() throws RiskCalculationException {
        product.calculateRisk(PREVIOUS);
        verify(riskCalculator, never()).calculateRisk(product, TODAY, marketValuation);
        verify(riskCalculator, times(1)).calculateRisk(product, PREVIOUS, marketValuation);
    }

    @Test(expected = RiskCalculationException.class)
    public void calculateRiskAsOfUnknown() throws RiskCalculationException {
        product.calculateRisk(FUTURE);
    }

    private class TestableLinearProduct extends LinearProduct {

        private int clientId;

        public TestableLinearProduct(MarketValuation marketValuation, TradeAction action, LocalDate today, LocalDate previous, RiskCalculator riskCalculator, int clientId) throws RiskCalculationException {
            super(marketValuation, action, today, previous, riskCalculator);
            this.clientId = clientId;
        }

        @Override
        public int getClientId() {
            return clientId;
        }
    }
}