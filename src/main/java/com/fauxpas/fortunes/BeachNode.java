package com.fauxpas.fortunes;

import com.fauxpas.geometry.Point;
import com.fauxpas.geometry.AdjacencyList;
import com.fauxpas.geometry.GNode;

import java.util.Optional;

public class BeachNode {

	private Point site;

    private BeachNode left;
    private BeachNode right;

    private FortuneEvent circleEvent;
    private AdjacencyList futureEdge;

    public BeachNode() {
    	this.site = null;
    	this.left = null;
    	this.right = null;
    	this.circleEvent = null;
    	this.futureEdge = null;
    }

    public void setSite(Point _s) {
        this.site = _s;
    }

    /**Set this Breakpoint's left arch
     *
     * @param _b arch branch to the left.
     */
    public void setLeft(BeachNode _b) {
        this.left = _b;
    }

    /**Set this Breakpoint's right arch.
     *
     * @param _b arch branch to the right.
     */
    public void setRight(BeachNode _b) {
        this.right = _b;
    }

    /**
     * Set the edge list this breakpoint will belong on.
     *
     * @param _al edge list this breakpoint belongs on.
     */
    public void setFutureEdge(AdjacencyList _al) {
        this.futureEdge = _al;
    }

    /**
     * Set the circle event linked to this leaf node.
     *
     * @param _e The circle event this leaf's arch will disappear at
     */
    public void setCircleEvent(FortuneEvent _e) {
        this.circleEvent = _e;
    }

    /**
     * Get the site for this arch of the beachline.
     *
     * @return the site that define's this leaf's arch.
     */
    public Point getSite() {
       return this.site;
    }

    /**
     * Get the arch to the left.
     *
     *@return the node that represents arch left of this breakpoint.
     */
    public BeachNode getLeft() {
    	return this.left;
    }

    /**
     * Get the arch to the right.
     *
     * @return the node that represents arch right of this breakpoint.
     */
    public BeachNode getRight() {
    	return this.right;
    }

    /**
     * Get the Circle event this leaf's arch will disappear.
     * Note that the circle event may not be detected yet.
     *
     * @return The event for this leaf (which may not be detected yet).
     */
    public Optional<FortuneEvent> getCircle() {
        return Optional.ofNullable(this.circleEvent);
    }

    /**
     * Get the end point on the opposite side of the end being traced by this futureEdge.
     *
     * Note: This may be null only becuase this BeachNode may be a leaf and not a break point.
     * Note: All breakpoints should return a futureEdge. 
     * Note: The return value is not a half edge and should be used to retrive and edge from the voronoi graph object.
     *
     * @return a vertex of the voronoi graph on the opposite end of this breakpoints traced edge.
     */
    public Optional<AdjacencyList> getFutureEdge() {
    	return Optional.ofNullable(this.futureEdge);
    }

    /**
     *  Check if this BeachNode has a left or right leaf, otherwise it's a leaf.
	 *
     *  @return True if this BeachNode is a leaf, false if it's a breakpoint.
     */
    public boolean isLeaf() {
    	return (this.left == null) && (this.right == null);
    }

    public double getX(Point _l) {
    	if (this.isLeaf()) {
    		return this.getLeafX();
    	}
    	else {
    		return this.getBreakPointX(_l);
    	}
    }

    public double getY(Point _l) {
        if (this.isLeaf()) {
            return this.getLeafY();
        }
        else {
            return this.getBreakPointY(_l);
        }
    }

    /**
     * Get the x Location of the site for this leaf (arch) in the beachline.
     *
     * @return X Location of the site generating this arch of the beach line.
     */
    public double getLeafX() {
        return this.getSite().x();
    }

    public double getLeafY() {
        return this.getSite().y();
    }

    /**
     * Get the x location of the breakpoint which is at
     * a point q the center of a circle passing through
     * left site, right site, and event (_l) site.
     *
     * note: to avoid degenerate cases (null pointer exception) look for a child which
     * has location.
     *
     * @param _l an event site to consider on the circle.
     *
     * @return x coordinate of the circle passing through left, right, and event sites.
     */
    public double getBreakPointX(Point _l) {

        //handle degenerate cases....
        if (this.left == null && this.right == null) {
            //this should hopefully never occur.
            return _l.x();
        }
        else if (this.left != null && this.right == null) {
            return this.left.getX(_l);
        }
        else if (this.left == null && this.right != null) {
            return this.right.getX(_l);
        }

        //calculate slope of points on circle. ma = (y2-y1)/(x2-x1) mb = (y3-y2)/(x3-x2)
        double ma = (_l.y() - this.left.getY(_l)) / ( _l.x() - this.left.getX(_l) );
        double mb = (this.right.getY(_l) - _l.y() ) / (this.right.getX(_l) - _l.x() );

        //calculate numerator of the x coordinates from perpendiculars to ma and mb
        double numerator = ma*mb*(this.left.getY(_l) - this.right.getY(_l)) +
                mb * (this.left.getX(_l) + _l.x()) -
                ma*( _l.x() + this.right.getX(_l) );

        // return x location of the circle.
        return numerator / (2* (mb - ma));
    }

    public double getBreakPointY(Point _l) {

        if (this.left == null && this.right == null) {
            //this should hopefully never occur.
            return _l.y();
        }
        else if (this.left != null && this.right == null) {
            return this.left.getY(_l);
        }
        else if (this.left == null && this.right != null) {
            return this.right.getY(_l);
        }

        double ma = (_l.y() - this.left.getY(_l)) / ( _l.x() - this.left.getX(_l) );
        return (-1 / ma) * (this.getBreakPointX(_l) - (this.left.getX(_l) + _l.x()) * 0.5) + (this.left.getY(_l) + _l.y()) * 0.5;
    }

}
