package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.model.*;

public class SwapGenerator implements PositionGenerator {


    @Override
    public Trade createPosition(Client client, int securityId, int position) throws RiskCalculationException {
        return new SwapTrade(securityId, ProductType.Swap, TradeAction.New, position);
    }
}
