package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.metrics.MetricsCalculator;
import com.jasonparrott.aggregateexposure.metrics.ProductMetrics;
import com.jasonparrott.aggregateexposure.model.position.Position;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;

public class SecurityGroupImpl extends HashSet<Position> implements SecurityGroup {
    private SecurityGroupId id;
    private MetricsCalculator metricsCalculator;
    private BigDecimal aggregatePosition = BigDecimal.ZERO;
    private ProductMetrics metrics;
    private Consumer<SecurityGroup> updateCallback;

    public SecurityGroupImpl(SecurityGroupId id, MetricsCalculator metricsCalculator, ProductMetrics initialMetrics) {
        this.id = id;
        this.metricsCalculator = metricsCalculator;
        this.metrics = initialMetrics;
    }

    @Override
    public SecurityGroupId getId() {
        return id;
    }

    @Override
    public ProductMetrics getMetrics() {
        return metrics;
    }

    @Override
    public BigDecimal getAggregatePosition() {
        return aggregatePosition;
    }

    @Override
    public boolean addAll(Collection<? extends Position> c) {
        BigDecimal sum = c.stream()
                .map(Position::getCurrentPosition)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        aggregatePosition = aggregatePosition.add(sum);
        return super.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        BigDecimal sum = c.stream()
                .filter(p -> p instanceof Position)
                .map(p -> ((Position) p).getCurrentPosition())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        aggregatePosition = aggregatePosition.subtract(sum);
        return super.removeAll(c);
    }

    @Override
    public Runnable updateMetrics() {
        return () -> revalue(metricsCalculator.calculateRisk(this, id.getToday(), id.getPrevious()));
    }

    public void setMetricsCalculator(MetricsCalculator metricsCalculator) {
        this.metricsCalculator = metricsCalculator;
    }

    protected void revalue(ProductMetrics metrics) {
        this.metrics = metrics;
        if (updateCallback != null)
            updateCallback.accept(this);
    }

    @Override
    public void unregisterUpdateCallback(Consumer<SecurityGroup> callback) {
        this.updateCallback = null;
    }

    @Override
    public void registerUpdateCallback(Consumer<SecurityGroup> callback) {
        this.updateCallback = callback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SecurityGroupImpl that = (SecurityGroupImpl) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(metricsCalculator, that.metricsCalculator) &&
                Objects.equals(aggregatePosition, that.aggregatePosition) &&
                Objects.equals(metrics, that.metrics) &&
                Objects.equals(updateCallback, that.updateCallback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, metricsCalculator, aggregatePosition, metrics, updateCallback);
    }

    @Override
    public String toString() {
        return "SecurityGroupImpl{" +
                "id=" + id +
                ", metricsCalculator=" + metricsCalculator +
                ", aggregatePosition=" + aggregatePosition +
                ", metrics=" + metrics +
                '}';
    }
}
