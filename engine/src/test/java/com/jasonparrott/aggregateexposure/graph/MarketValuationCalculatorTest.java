package com.jasonparrott.aggregateexposure.graph;

import com.jasonparrott.aggregateexposure.model.MarketValuation;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MarketValuationCalculatorTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private MarketValuation valuation = new MarketValuation("", 10.0d);

    @Test
    public void testEquality() {
        MarketValuationCalculator calculator = new MarketValuationCalculator(valuation, null);
        assertThat(calculator, is(valuation));
    }
}