package com.jasonparrott.aggregateexposure.graph;

import com.jasonparrott.aggregateexposure.TradeUpdateManager;
import com.jasonparrott.aggregateexposure.model.Trade;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BaseCalculator implements Calculator {
    private final List<Trade> listeners = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Collection<Calculator> inputs;
    private final TradeUpdateManager updateManager;
    private CalculationResult result;

    protected BaseCalculator(Collection<Calculator> inputs, TradeUpdateManager updateManager) {
        this.inputs = inputs;
        this.updateManager = updateManager;
    }

    @Override
    public TradeUpdateManager getUpdateManager() {
        return updateManager;
    }

    @Override
    public CalculationResult getCalculationResult() {
        return result;
    }

    @Override
    public void calculate() {
        result = doCalculation();
        lock.lock();
        try {
            listeners.forEach(updateManager::updateTrade);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void registerForChanges(Trade listener) {
        lock.lock();
        try {
            listeners.add(listener);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unregisterForChanges(Trade listener) {
        lock.lock();
        try {
            listeners.remove(listener);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<Calculator> getInputs() {
        return inputs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseCalculator that = (BaseCalculator) o;
        return Objects.equals(inputs, that.inputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputs);
    }

    protected abstract CalculationResult doCalculation();
}
