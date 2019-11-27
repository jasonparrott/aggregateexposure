package com.jasonparrott.aggregateexposure.graph;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class CalculationNode {
    private final ReentrantLock lock = new ReentrantLock();
    private final Calculator calculator;

    public CalculationNode(Calculator calculator) {
        this.calculator = calculator;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public Calculator getCalculator() {
        return calculator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculationNode that = (CalculationNode) o;
        return Objects.equals(calculator, that.calculator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calculator);
    }
}
