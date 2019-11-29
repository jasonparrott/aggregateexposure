package com.jasonparrott.aggregateexposure.graph;

import com.jasonparrott.aggregateexposure.SecurityGroupUpdateManager;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BaseCalculator implements Calculator {
    private final List<SecurityGroup> listeners = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Collection<Calculator> inputs;
    private final SecurityGroupUpdateManager updateManager;
    private final String label;
    private CalculationResult result;

    protected BaseCalculator(Collection<Calculator> inputs, SecurityGroupUpdateManager updateManager, String label) {
        this.inputs = inputs;
        this.updateManager = updateManager;
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public SecurityGroupUpdateManager getUpdateManager() {
        return updateManager;
    }

    @Override
    public CalculationResult getCalculationResult() {
        if (result == null)
            result = doCalculation();

        return result;
    }

    @Override
    public void calculate() {
        lock.lock();
        try {
            result = doCalculation();
            if (listeners != null && !listeners.isEmpty())
                listeners.forEach(updateManager::updateSecurityGroup);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void registerForChanges(SecurityGroup listener) {
        lock.lock();
        try {
            listeners.add(listener);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unregisterForChanges(SecurityGroup listener) {
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

        return that.getInputs().size() == getInputs().size() &&
                that.getInputs().containsAll(getInputs()) &&
                getInputs().containsAll(that.getInputs());
    }

    @Override
    public int hashCode() {
        return inputs.hashCode();
    }

    protected abstract CalculationResult doCalculation();
}
