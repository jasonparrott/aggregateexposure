package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.trade.Trade;

import java.util.Collection;
import java.util.function.Supplier;

public interface TradeListener extends Supplier<Collection<Trade>> {

}
