package com.fauxpas.geometry;

import java.util.Optional;

public class GNode {

    private Point location;

    public GNode(Point _p) {
        this.setLocation(_p);
    }

    public GNode(double _x, double _y) {
        this.setLocation(new Point(_x, _y));
    }

    public Point location(){
        return this.location;
    }

    public void setLocation(Point _p) {
        this.location = _p;
    }

    public void setLocation(double _x, double _y) {
        this.setLocation(new Point(_x, _y) );
    }

    public boolean isSameNode(GNode _v) {
        return this.location().effectivelyEqual(_v.location(), 0.01);
    }
}
