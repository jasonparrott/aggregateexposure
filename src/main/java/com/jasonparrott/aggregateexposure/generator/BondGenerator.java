package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.model.*;

public class BondGenerator implements PositionGenerator {
    @Override
    public Trade createPosition(Client client, int securityId, int position) throws RiskCalculationException {
        return new BondTrade(securityId, ProductType.Bond, TradeAction.New, position);
    }
}
