package com.fauxpas.geometry;

import java.util.Optional;

public class GNode {

    private Point location;

    public GNode(Point _p) {
        this.setLocation(_p);
    }

    public Point location(){
        return this.location;
    }

    public void setLocation(Point _p) {
        this.location = _p;
    }

    public boolean isSameNode(GNode _v) {
        return this.location().effectivelyEqual(_v.location(), 0.01);
    }
}
