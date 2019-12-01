package com.jasonparrott.aggregateexposure.model.position;

import com.jasonparrott.aggregateexposure.model.trade.Trade;

import java.math.BigDecimal;
import java.util.Objects;

public class BondPosition implements Position {

    private final BigDecimal currentPosition;
    private final Trade underlyingTrade;

    public BondPosition(BigDecimal currentPosition, Trade underlying) {
        this.currentPosition = currentPosition;
        this.underlyingTrade = underlying;
    }

    public BigDecimal getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public Trade getUnderlyingTrade() {
        return underlyingTrade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BondPosition that = (BondPosition) o;
        return Objects.equals(currentPosition, that.currentPosition) &&
                Objects.equals(underlyingTrade, that.underlyingTrade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPosition, underlyingTrade);
    }

    @Override
    public String toString() {
        return "SwapPosition{" +
                "currentPosition=" + currentPosition +
                ", underlyingTrade=" + underlyingTrade +
                '}';
    }
}
