package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.product.MetricsCalculator;
import com.jasonparrott.aggregateexposure.calculators.product.ProductMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class LinearProduct implements Trade {
    private static DateTimeFormatter ddMMMyy = DateTimeFormatter.ofPattern("dd-MMM-yy");
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final LocalDate today;
    private final LocalDate previous;
    private final MetricsCalculator metricsCalculator;
    private ProductMetrics metrics;
    private TradeAction action;
    private Consumer<Double> updateCallback;

    public LinearProduct(TradeAction action, LocalDate today, LocalDate previous, MetricsCalculator metricsCalculator) throws RiskCalculationException {
        this.action = action;
        this.today = today;
        this.previous = previous;
        this.metricsCalculator = metricsCalculator;
        updateMetrics().run(); // initial values
    }

    public Runnable updateMetrics() {
        return () -> revalue(metricsCalculator.calculateRisk(this, today, previous));
    }

    protected void revalue(ProductMetrics metrics) {
        this.metrics = metrics;
        updateCallback.accept(metrics.getIntradayChange());
    }

    public ProductMetrics getMetrics() {
        return metrics;
    }

    @Override
    public void updateTradeAction(TradeAction update) {
        action = update;
        // not on a separate thread under the assumption that this doesnt really happen that often
        updateMetrics().run();
    }

    @Override
    public void registerUpdateCallback(Consumer<Double> callback) {
        this.updateCallback = callback;
    }

    @Override
    public void unregisterUpdateCallback(Consumer<Double> callback) {
        this.updateCallback = null;
    }

    @Override
    public TradeAction getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinearProduct that = (LinearProduct) o;
        return Objects.equals(today, that.today) &&
                Objects.equals(previous, that.previous) &&
                Objects.equals(metricsCalculator, that.metricsCalculator) &&
                action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(today, previous, metricsCalculator, action);
    }
}
