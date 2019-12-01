package com.jasonparrott.aggregateexposure.metrics;

import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class AnnotationBasedMetricsCalculatorFactoryTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private final SecurityGroupId BONDID = new SecurityGroupId(0, ProductType.Bond, LocalDate.now(), LocalDate.now());
    private final SecurityGroupId SWAPID = new SecurityGroupId(0, ProductType.Swap, LocalDate.now(), LocalDate.now());

    private AnnotationBasedMetricsCalculatorFactory factory;

    @Before
    public void setup() {
        factory = new AnnotationBasedMetricsCalculatorFactory();
    }

    @Test
    public void testKnownCalculator() throws Exception {
        MetricsCalculator calculator = factory.forSecurityGroup(BONDID, null);
        assertThat(calculator, is(not(nullValue())));
        assertThat(calculator, instanceOf(TestBondMetricsCalculatorFactory.TestBondMetricsCalculator.class));
    }

    @Test(expected = NullPointerException.class)
    public void testUnknownCalculator() throws Exception {
        factory.forSecurityGroup(SWAPID, null);
    }
}