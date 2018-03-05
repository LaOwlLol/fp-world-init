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
        this.site = _s;
        this.left = null;
        this.right = null;
        this.endPoint = null;
    }

    /**
     * Set the edge list this breakpoint will belong on.
     *
     * @param _al edge list this breakpoint belongs on.
     */
    public void setEndPoint(AdjacencyList _al) {
        this.endPoint = _al;
    }

    /**
     * Get the edge list this breakpoint will belong on.
     *
     * @return Optional edge list to add to when this breakpoint is completed..
     */

    public Optional<AdjacencyList> getEndPoint() {
        return Optional.ofNullable(this.endPoint);
    }

    /**Set this Breakpoint's left arch
     *
     * @param _b arch branch to the left.
     */
    public void setLeft(BeachNode _b) {
        this.left = _b;
    }

    /**Get this Breakpoint's left arch.
     *
     * @return Optional arc to the left.
     */
    public Optional<BeachNode> getLeft() {
        return Optional.ofNullable(left);
    }

    /**Set this Breakpoint's right arch.
     *
     * @param _b arch branch to the right.
     */
    public void setRight(BeachNode _b) {
        this.right = _b;
    }

    /**Set this BreakPoint's right arch.
     *
     * @return Optional arch to the right.
     */
    public Optional<BeachNode> getRight() { return Optional.ofNullable(right); }

    /**
     *
     * @return the site associated with the middle (collapsing?) arch.
     */
    public Point getSite() {
        return this.site;
    }

    /**
     * Get the location of the breakpoint which is at
     * a point q being the center of a circle passing through
     * left site, right site, and event site.
     *
     * note: to avoid degenerate cases (null pointer exception) return this.site.x()
     * if left or right is not set.
     *
     * @param _l an event site to calculate the break point
     * @return x coordinate of the circle passing through left, right, and event sites.
     */
    @Override
    public double getX(Point _l) {

        if (this.left == null || this.right == null) {
            return this.site.x();
        }

        //calculate slope of points on circle. ma = (y2-y1)/(x2-x1) mb = (y3-y2)/(x3-x2)
        double ma = (_l.y() - this.left.getSite().y()) / ( _l.x() - this.left.getSite().x() );
        double mb = (this.right.getSite().y() - _l.y() ) / (this.right.getSite().x() - _l.x() );

        //calculate numerator of the x coordinates from perpendiculars to ma and mb
        double numerator = ma*mb*(this.left.getSite().y() - this.right.getSite().y()) +
                mb * (this.left.getSite().x() + _l.x()) -
                ma*( _l.x() + this.right.getSite().x() );

        // return x location of the circle.
        return numerator / (2* (mb - ma));
    }
}
