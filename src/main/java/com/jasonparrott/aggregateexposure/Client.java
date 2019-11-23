package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.Position;

import java.util.Objects;

public class Client {
    private final int id;
    private Position[] positions;
    private FenwickTree valuationTree;

    public Client(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Position[] getPositions() {
        return positions;
    }

    public void setPositions(Position[] positions) {
        this.positions = positions;
        for(int i = 0; i < positions.length; ++i) {
            int finalI = i;
            positions[i].registerUpdateCallback((diff) -> {
                try {
                    valuationTree.update(finalI, diff);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            });
        }
        valuationTree = new FenwickTree(positions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
