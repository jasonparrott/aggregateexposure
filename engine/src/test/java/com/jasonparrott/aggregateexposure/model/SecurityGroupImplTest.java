package com.jasonparrott.aggregateexposure.model;

import com.google.common.collect.ImmutableList;
import com.jasonparrott.aggregateexposure.metrics.MetricsCalculator;
import com.jasonparrott.aggregateexposure.model.position.BondPosition;
import com.jasonparrott.aggregateexposure.model.trade.Trade;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SecurityGroupImplTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private MetricsCalculator metricsCalculator;

    @Mock
    private Trade trade;

    private final SecurityGroupId ID = new SecurityGroupId(0, ProductType.Bond, LocalDate.now(), LocalDate.now());

    private final BondPosition POS1 = new BondPosition(BigDecimal.valueOf(50L), trade);
    private final BondPosition POS2 = new BondPosition(BigDecimal.valueOf(25L), trade);

    private SecurityGroupImpl securityGroup;

    @Before
    public void setup() {
        securityGroup = new SecurityGroupImpl(ID, metricsCalculator, null);
    }

    @Test
    public void testInitialAggregatePosition() {
        assertThat(securityGroup.getAggregatePosition(), is(BigDecimal.ZERO));
    }

    @Test
    public void testAddAllSumsConstituentPositions() {
        securityGroup.addAll(ImmutableList.of(POS1, POS2));
        assertThat(securityGroup.getAggregatePosition(), is(BigDecimal.valueOf(75L)));
    }

    @Test
    public void testRemoveAllRemovesConstituentPositions() {
        securityGroup.addAll(ImmutableList.of(POS1, POS2));
        securityGroup.removeAll(ImmutableList.of(POS2));

        assertThat(securityGroup.getAggregatePosition(), is(BigDecimal.valueOf(50L)));
    }

    @Test
    public void testRemoveAllSkipsNonPositionElements() {
        securityGroup.addAll(ImmutableList.of(POS1, POS2));
        securityGroup.removeAll(ImmutableList.of(POS2, new Object()));

        assertThat(securityGroup.getAggregatePosition(), is(BigDecimal.valueOf(50L)));
    }

    @Test
    public void testUpdateMetricsReturnsRunnableRecalculatingValues() {
        Runnable runnable = securityGroup.updateMetrics();
        assertThat(runnable, is(not(nullValue())));

        runnable.run();
        verify(metricsCalculator, times(1)).calculateRisk(securityGroup, LocalDate.now(), LocalDate.now());
    }

    @Test
    public void testRevalueCallsCallback() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Consumer<SecurityGroup> callback = securityGroup1 -> latch.countDown();
        securityGroup.registerUpdateCallback(callback);
        securityGroup.updateMetrics().run();
        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }
}