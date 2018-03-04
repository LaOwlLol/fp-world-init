package com.fauxpas.fortunes;

import com.fauxpas.geometry.AdjacencyList;
import com.fauxpas.geometry.GNode;

import java.util.Optional;

public class BeachBreak extends BeachNode {

    private BeachLeaf left;
    private BeachLeaf right;
    private Optional<AdjacencyList> endPoint;

    public BeachBreak(BeachLeaf _l, BeachLeaf _r) {
        this.left = _l;
        this.right = _r;

        this.endPoint = Optional.empty();
    }

    public void setEndPoint(Optional<AdjacencyList> _al) {
        this.endPoint = _al;
    }

    public Optional<AdjacencyList> getEndPoint() {
        return endPoint;
    }

    public BeachLeaf getLeft() {
        return left;
    }

    public BeachLeaf getRight() {
        return right;
    }

    @Override
    public double getX() {
        //todo this is not right at all.
        return 0.0;
    }
}
