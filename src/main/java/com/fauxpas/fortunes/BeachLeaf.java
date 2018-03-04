package com.fauxpas.fortunes;

import com.fauxpas.geometry.Point;

import java.util.Optional;

public class BeachLeaf extends BeachNode {

    private Point site;
    private FortuneEvent circleEvent;

    /**
     * Note: Leafs require their circle event to be
     * set after construction.
     *
     * * @param _s the site generating this arch of the beachline.
     */
    public BeachLeaf(Point _s) {
        this.site = _s;
        this.circleEvent = null;
    }

    /**
     *
     * @param _e The circle event this leaf's arch will disappear at
     */
    public void setCircleEvent(FortuneEvent _e) {
        this.circleEvent = _e;
    }

    /**
     * Get the Circle event this leaf's arch will disappear.
     * Note that the circle event may not be detected yet.
     *
     * @return The event for this leaf (which may not exist yet).
     */
    public Optional<FortuneEvent> getCircle() {
        return Optional.ofNullable(this.circleEvent);
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
     * Get the x Location of the site for this arch in the beachline.
     * This can be use for sorting.
     *
     * @return X Location of the site generating this arch of the beach line.
     */
    @Override
    public double getX(Point _l) {
        return this.getSite().x();
    }
}
