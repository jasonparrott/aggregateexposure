package com.jasonparrott.aggregateexposure.graph.mapper;

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

public class AnnotationBasedValuationMapperFactoryTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private final SecurityGroupId BONDID = new SecurityGroupId(0, ProductType.Bond, LocalDate.now(), LocalDate.now());
    private final SecurityGroupId SWAPID = new SecurityGroupId(0, ProductType.Swap, LocalDate.now(), LocalDate.now());

    private AnnotationBasedValuationMapperFactory factory;

    @Before
    public void setup() {
        factory = new AnnotationBasedValuationMapperFactory();
    }

    @Test
    public void testKnownBondMapper() throws Exception {
        SecurityGroupValuationMapper mapper = factory.forSecurityGroup(BONDID);
        assertThat(mapper, is(not(nullValue())));
    }

    @Test(expected = NullPointerException.class)
    public void testUnknownSwapMapper() throws Exception {
        factory.forSecurityGroup(SWAPID);
    }
}