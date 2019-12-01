package com.jasonparrott.aggregateexposure.position;

import com.jasonparrott.aggregateexposure.model.position.Position;
import com.jasonparrott.aggregateexposure.model.trade.Trade;

public interface PositionService {
    Position getPositionForTrade(Trade trade);
}
