package com.jasonparrott.aggregateexposure.graph;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CalculationNodeTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private Calculator calculator;

    @Test
    public void testLockAndUnlock() throws InterruptedException {
        CalculationNode node = new CalculationNode(calculator);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        new Thread(() -> {
            node.lock();
            try {
                latch2.await();
            } catch (InterruptedException e) {
                // nothing
            }
            node.unlock();
        }).start();

        new Thread(() -> {
            node.lock();
            latch.countDown();
        }).start();

        assertThat(latch.getCount(), is(1L));
        latch2.countDown();
        assertThat(latch.await(500, TimeUnit.MILLISECONDS), is(true));
    }
}