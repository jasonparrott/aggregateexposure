package com.jasonparrott.aggregateexposure.position;

import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.position.BondPosition;
import com.jasonparrott.aggregateexposure.model.position.Position;
import com.jasonparrott.aggregateexposure.model.trade.Trade;

import java.math.BigDecimal;

@PositionProducer(productType = ProductType.Swap)
public class RandomSwapPositionService implements PositionService {
    @Override
    public Position getPositionForTrade(Trade trade) {
        BigDecimal position = trade.getSize().multiply(BigDecimal.valueOf(1d + 2 * Math.random()));
        return new BondPosition(position, trade);
    }
}
