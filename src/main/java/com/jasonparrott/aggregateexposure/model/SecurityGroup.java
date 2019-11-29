package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.calculators.product.DefaultProductMetrics;
import com.jasonparrott.aggregateexposure.calculators.product.MetricsCalculator;
import com.jasonparrott.aggregateexposure.calculators.product.ProductMetrics;

import java.util.HashSet;
import java.util.function.Consumer;

public abstract class SecurityGroup<T extends Trade> extends HashSet<T> {
    private final int securityId;
    private final ProductType productType;

    private long aggregatePosition = 0L;
    private ProductMetrics metrics = new DefaultProductMetrics(0, 0, 0);

    public SecurityGroup(int securityId, ProductType productType) {
        this.securityId = securityId;
        this.productType = productType;
    }

    public int getSecurityId() {
        return securityId;
    }

    public ProductMetrics getMetrics() {
        return metrics;
    }

    public long getAggregatePosition() {
        return aggregatePosition;
    }

//    public void setMetrics(ProductMetrics metrics) {
//        this.metrics = metrics;
//    }

    public ProductType getProductType() {
        return productType;
    }

    @Override
    public boolean add(T trade) {
        aggregatePosition += trade.getPosition();
        return super.add(trade);
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Trade) {
            T trade = (T) o;
            aggregatePosition -= trade.getPosition();
        }
        return super.remove(o);
    }

    public abstract void registerUpdateCallback(Consumer<Double> callback);

    public abstract void unregisterUpdateCallback(Consumer<Double> callback);

    public abstract void setMetricsCalculator(MetricsCalculator metricsCalculator);

    public abstract Runnable updateMetrics();
}
