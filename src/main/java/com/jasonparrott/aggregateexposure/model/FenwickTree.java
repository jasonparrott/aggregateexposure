package com.jasonparrott.aggregateexposure.model;

import com.jasonparrott.aggregateexposure.model.Trade;

import java.util.concurrent.locks.ReentrantLock;

public class FenwickTree {

    private PositionNode[] tree;
    private ReentrantLock lock = new ReentrantLock();
    public FenwickTree(Trade[] trades) {
        tree = new PositionNode[trades.length + 1];
        for (int i = 1; i <= trades.length; i++)
            tree[i] = new PositionNode(0);

        try {
            for (int i = 0; i < trades.length; ++i) {
                update(i, trades[i].getOpenRisk());
            }
        } catch (InterruptedException ie) {
            Thread.interrupted();
        }
    }

    public void update(int i, int difference) throws InterruptedException {
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
        private int value;

        PositionNode(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }

        void setValue(int value) {
            this.value = value;
        }
    }
}
