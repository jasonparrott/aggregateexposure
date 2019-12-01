package com.jasonparrott.aggregateexposure.position;

import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.position.Position;
import com.jasonparrott.aggregateexposure.model.trade.Trade;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

public class AnnotationBasedPositionServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private Trade trade;

    private AnnotationBasedPositionService service;

    @Before
    public void setup() {
        service = new AnnotationBasedPositionService();
    }

    @Test
    public void testKnownProductType() {
        doReturn(BigDecimal.TEN).when(trade).getSize();
        doReturn(ProductType.Bond).when(trade).getProductType();
        Position position = service.getPositionForTrade(trade);
        assertThat(position, is(not(nullValue())));
    }

    @Test(expected = NullPointerException.class)
    public void testUnknownProductType() {
        doReturn(ProductType.Swap).when(trade).getProductType();
        service.getPositionForTrade(trade);
    }

}