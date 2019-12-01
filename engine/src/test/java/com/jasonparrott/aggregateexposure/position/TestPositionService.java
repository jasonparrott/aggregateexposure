package com.jasonparrott.aggregateexposure.position;

import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.position.BondPosition;
import com.jasonparrott.aggregateexposure.model.position.Position;
import com.jasonparrott.aggregateexposure.model.trade.Trade;

@PositionProducer(productType = ProductType.Bond)
public class TestPositionService implements PositionService {
    @Override
    public Position getPositionForTrade(Trade trade) {
        return new BondPosition(trade.getSize(), trade);
    }
}
