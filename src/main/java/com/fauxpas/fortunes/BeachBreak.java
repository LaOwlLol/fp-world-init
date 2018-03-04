package com.fauxpas.fortunes;

import com.fauxpas.geometry.AdjacencyList;
import com.fauxpas.geometry.Point;

import java.util.Optional;

public class BeachBreak extends BeachNode {

    private BeachNode left;
    private BeachNode right;
    private Point site;
    private AdjacencyList endPoint;

    public BeachBreak(Point _s) {
        this.left = null;
        this.right = null;
        this.site = _s;
        this.endPoint = null;
    }

    public void setEndPoint(AdjacencyList _al) {
        this.endPoint = _al;
    }

    public Optional<AdjacencyList> getEndPoint() {
        return Optional.ofNullable(this.endPoint);
    }

    public void setLeft(BeachNode _b) {
        this.left = _b;
    }

    public Optional<BeachNode> getLeft() {
        return Optional.ofNullable(left);
    }

    public void setRight(BeachNode _b) {
        this.right = _b;
    }

    public Optional<BeachNode> getRight() { return Optional.ofNullable(right); }

    public Point getSite() {
        return this.site;
    }

    /**
     * Get the location of the breakpoint which is at
     * a point q being the center of a circle passing through
     * left site, right site, and event site.
     *
     * @param _l an event site to calculate the break point
     * @return
     */
    @Override
    public double getX(Point _l) {
        double ma = (_l.y() - this.left.getSite().y()) / ( _l.x() - this.left.getSite().x() );
        double mb = (this.right.getSite().y() - _l.y() ) / (this.right.getSite().x() - _l.x() );
        double numerator = ma*mb*(this.left.getSite().y() - this.right.getSite().y()) +
                mb * (this.left.getSite().x() + _l.x()) -
                ma*( _l.x() + this.right.getSite().x() );
        return numerator / (2* (mb - ma));
    }
}
