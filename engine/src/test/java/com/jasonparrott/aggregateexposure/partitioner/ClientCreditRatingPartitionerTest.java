package com.jasonparrott.aggregateexposure.partitioner;

import com.jasonparrott.aggregateexposure.BusinessDateService;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.CreditRating;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import com.jasonparrott.aggregateexposure.model.position.Position;
import com.jasonparrott.aggregateexposure.model.trade.Trade;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

public class ClientCreditRatingPartitionerTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private BusinessDateService businessDateService;

    @Mock
    private Trade trade;

    @Mock
    private Position position;

    private ClientCreditRatingPartitioner partitioner;
    private Client CLIENT1 = new Client(0, CreditRating.AA);
    private Client CLIENT2 = new Client(0, CreditRating.AA); // same as Client1
    private Client CLIENT3 = new Client(0, CreditRating.BB); // not same as Client1

    @Before
    public void setup() {
        partitioner = new ClientCreditRatingPartitioner(businessDateService);
        doReturn(trade).when(position).getUnderlyingTrade();
    }

    @Test
    public void testUniqueIdsForDifferentClientCreditRatings() {
        doReturn(CLIENT1).when(trade).getClient();
        SecurityGroupId first = partitioner.findGroupForPosition(position);
        doReturn(CLIENT3).when(trade).getClient();
        SecurityGroupId other = partitioner.findGroupForPosition(position);

        assertThat(first, is(not(other)));
    }

    @Test
    public void testIdenticalKeysForIdenticalCreditRatings() {
        doReturn(CLIENT1).when(trade).getClient();
        SecurityGroupId first = partitioner.findGroupForPosition(position);
        doReturn(CLIENT2).when(trade).getClient();
        SecurityGroupId other = partitioner.findGroupForPosition(position);

        assertThat(first, is(other));
    }

}