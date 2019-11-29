package com.jasonparrott.aggregateexposure.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class CalculationNode {
    private final ReentrantLock lock = new ReentrantLock();
    private final Calculator calculator;
    private Graph<CalculationNode, DefaultEdge> subgraph;

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

    public String getLabel() {
        return calculator.getLabel();
    }

    public Graph<CalculationNode, DefaultEdge> getSubgraph() {
        return subgraph;
    }

    public void setSubgraph(Graph<CalculationNode, DefaultEdge> subgraph) {
        this.subgraph = subgraph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Calculator) {
            return Objects.equals(calculator, o);
        }
        if (o == null || getClass() != o.getClass()) return false;
        CalculationNode that = (CalculationNode) o;
        return Objects.equals(getLabel(), that.getLabel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLabel());
    }
}
