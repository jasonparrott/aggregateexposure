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

import static com.google.common.collect.ImmutableList.of;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class StaticValuationMapperFactoryTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private final SecurityGroupValuationMapper TESTMAPPER = new TestBondValuationMapperFactory.TestBondMapper();
    private final SecurityGroupId BONDID = new SecurityGroupId(0, ProductType.Bond, LocalDate.now(), LocalDate.now());
    private final SecurityGroupId SWAPID = new SecurityGroupId(0, ProductType.Swap, LocalDate.now(), LocalDate.now());

    private StaticValuationMapperFactory factory;

    @Before
    public void setup() {
        factory = new StaticValuationMapperFactory(of(TESTMAPPER));
    }

    @Test(expected = Exception.class)
    public void testUnknownProductTypeThrows() throws Exception {
        factory.forSecurityGroup(SWAPID);
    }

    @Test
    public void testKnownProductTypeResturnsExpected() throws Exception {
        SecurityGroupValuationMapper mapper = factory.forSecurityGroup(BONDID);
        assertThat(mapper, is(not(nullValue())));
        assertThat(mapper, is(TESTMAPPER));

    }
}