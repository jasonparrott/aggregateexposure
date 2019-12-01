package com.jasonparrott.aggregateexposure.metrics;

import com.google.common.collect.ImmutableList;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ChainedMetricsCalculatorTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private MetricsCalculator calculator1;

    @Mock
    private MetricsCalculator calculator2;

    @Mock
    private SecurityGroup securityGroup;

    @Test
    public void testEmptyCalculatorsDoesNotThrow() {
        ChainedMetricsCalculator chain = new ChainedMetricsCalculator(Collections.emptyList());
        ProductMetrics metrics = chain.calculateRisk(securityGroup, LocalDate.now(), LocalDate.now());
        assertThat(metrics.getIntradayRisk(), is(BigDecimal.ZERO));
        assertThat(metrics.getIntradayChange(), is(BigDecimal.ZERO));
        assertThat(metrics.getOpenRisk(), is(BigDecimal.ZERO));
    }

    @Test
    public void testCalculatorsCalledInOrder() {
        ProductMetrics result1 = new DefaultProductMetrics(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN);
        ProductMetrics result2 = new DefaultProductMetrics(BigDecimal.valueOf(5L), BigDecimal.valueOf(5L), BigDecimal.valueOf(5L));

        doReturn(result1).when(calculator1).calculateRisk(securityGroup, LocalDate.now(), LocalDate.now());
        doReturn(result2).when(calculator2).calculateRisk(securityGroup, LocalDate.now(), LocalDate.now());

        ChainedMetricsCalculator chain = new ChainedMetricsCalculator(ImmutableList.of(calculator1, calculator2));
        ProductMetrics testResult = chain.calculateRisk(securityGroup, LocalDate.now(), LocalDate.now());

        final ProductMetrics EXPECTED = new DefaultProductMetrics(BigDecimal.valueOf(5L), BigDecimal.valueOf(15L), BigDecimal.valueOf(15L));

        verify(calculator1, times(1)).calculateRisk(securityGroup, LocalDate.now(), LocalDate.now());
        verify(calculator2, times(1)).calculateRisk(securityGroup, LocalDate.now(), LocalDate.now());

        assertThat(testResult, is(EXPECTED));

    }

}