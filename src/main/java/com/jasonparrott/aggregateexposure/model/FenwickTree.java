package com.jasonparrott.aggregateexposure.model;

import java.util.concurrent.locks.ReentrantLock;

public class FenwickTree {

    private PositionNode[] tree;
    private ReentrantLock lock = new ReentrantLock();

    public FenwickTree(SecurityGroup[] tradeGroups) {
        tree = new PositionNode[tradeGroups.length + 1];
        for (int i = 1; i <= tradeGroups.length; i++)
            tree[i] = new PositionNode(0);

        try {
            for (int i = 0; i < tradeGroups.length; ++i) {
                update(i, tradeGroups[i].getMetrics().getOpenRisk());
            }
        } catch (InterruptedException ie) {
            Thread.interrupted();
        }
    }

    public void update(int i, double difference) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            int index = i + 1;
            while (index <= (tree.length - 1)) {
                tree[index].setValue(tree[index].getValue() + difference);
                index += index & (-index);
            }
        } finally {
            lock.unlock();
        }
    }

    public int sum(int index) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            int sum = 0;
            index = index + 1;

            while (index > 0) {
                sum += tree[index].getValue();
                index -= index & (-index);
            }

            return sum;
        } finally {
            lock.unlock();
        }
    }

    private class PositionNode {
        private double value;

        PositionNode(double value) {
            this.value = value;
        }

        double getValue() {
            return value;
        }

        void setValue(double value) {
            this.value = value;
        }
    }
}
