package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.RiskCalculationException;
import com.jasonparrott.aggregateexposure.calculators.product.DefaultProductMetrics;
import com.jasonparrott.aggregateexposure.calculators.product.MetricsCalculator;
import com.jasonparrott.aggregateexposure.calculators.product.ProductMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.function.Consumer;

public class LinearSecurityGroup extends SecurityGroup implements Riskable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final LocalDate today;
    private final LocalDate previous;
    private MetricsCalculator metricsCalculator;
    private ProductMetrics metrics = new DefaultProductMetrics(0, 0, 0);
    private Consumer<Double> updateCallback;

    public LinearSecurityGroup(int securityId, ProductType productType, LocalDate today, LocalDate previous) throws RiskCalculationException {
        super(securityId, productType);
        this.today = today;
        this.previous = previous;
    }

    public LinearSecurityGroup(int securityId, ProductType productType, LocalDate today, LocalDate previous, MetricsCalculator metricsCalculator) throws RiskCalculationException {
        super(securityId, productType);
        this.today = today;
        this.previous = previous;
        this.metricsCalculator = metricsCalculator;
    }

    @Override
    public void setMetricsCalculator(MetricsCalculator metricsCalculator) {
        this.metricsCalculator = metricsCalculator;
    }

    @Override
    public Runnable updateMetrics() {
        return () -> revalue(metricsCalculator.calculateRisk(this, today, previous));
    }

    protected void revalue(ProductMetrics metrics) {
        this.metrics = metrics;
        if (updateCallback != null)
            updateCallback.accept(metrics.getIntradayChange());
    }

    public ProductMetrics getMetrics() {
        return metrics;
    }

    @Override
    public void unregisterUpdateCallback(Consumer callback) {
        this.updateCallback = null;
    }

    @Override
    public void registerUpdateCallback(Consumer callback) {
        this.updateCallback = callback;
    }

}
