package com.jasonparrott.aggregateexposure.model.position;

import com.jasonparrott.aggregateexposure.model.trade.Trade;

import java.math.BigDecimal;

public interface Position {
    BigDecimal getCurrentPosition();

    Trade getUnderlyingTrade();
}
