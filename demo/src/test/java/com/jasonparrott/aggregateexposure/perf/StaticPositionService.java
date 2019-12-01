package com.jasonparrott.aggregateexposure.perf;

import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.position.BondPosition;
import com.jasonparrott.aggregateexposure.model.position.Position;
import com.jasonparrott.aggregateexposure.model.position.SwapPosition;
import com.jasonparrott.aggregateexposure.model.trade.Trade;
import com.jasonparrott.aggregateexposure.position.PositionService;

import java.math.BigDecimal;

public class StaticPositionService implements PositionService {
    @Override
    public Position getPositionForTrade(Trade trade) {
        if (trade.getProductType().equals(ProductType.Bond))
            return new BondPosition(BigDecimal.TEN, trade);
        else
            return new SwapPosition(BigDecimal.TEN, trade);
    }
}
