package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.Position;

import java.util.concurrent.locks.ReentrantLock;

public class FenwickTree {

    private class PositionNode {
        private final Position position;
        private int value;

        PositionNode(Position position, int index, int value) {
            this.position = position;
            this.value = value;
        }

        int getExposure() {
            return position.getExposure();
        }

        int getValue() {
            return value;
        }

        void setValue(int value) {
            this.value = value;
        }
    }

    private PositionNode[] tree;
    private ReentrantLock lock = new ReentrantLock();

    public FenwickTree(Position[] positions)  {
        tree = new PositionNode[positions.length + 1];
        for(int i = 1; i <= positions.length; i++)
            tree[i] = new PositionNode(positions[i-1], i, 0);

        try {
            for (int i = 0; i < positions.length; ++i) {
                update(i, positions[i].getExposure());
            }
        } catch (InterruptedException ie) {
            Thread.interrupted();
        }
    }

    public void update(int i, int difference) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            int index = i + 1;
//            int value = tree[index].getExposure();
            while (index <= (tree.length-1)) {
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

//    private class PositionUpdateCallback implements Runnable {
//        private final int index;
//        private final FenwickTree tree;
//
//        private PositionUpdateCallback(int index, FenwickTree tree) {
//            this.index = index;
//            this.tree = tree;
//        }
//
//        @Override
//        public void run() {
//            try {
//                tree.update(index);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
