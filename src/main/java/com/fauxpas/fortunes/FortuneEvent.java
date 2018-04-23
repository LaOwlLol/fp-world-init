package com.fauxpas.fortunes;

import com.fauxpas.geometry.Point;

import java.util.Optional;

public class FortuneEvent {

    private Point site;
    private BeachNode archLeaf;

    /**
     * Note to construct this event as a circle event that collapses
     * an arch of the beachline, use setArchLeaf to associate the
     * collapsed arch of the beachline.
     *
     * @param _p site of this event.
     */
    public FortuneEvent(Point _p) {
        this.site = _p;
    }

    /**
     * Set an associated arch of the beachline to make this
     * a circle event.
     *
     * @param _l the arch leaf associated with this circle event.
     */
    public void setArchLeaf(BeachNode _l) {
        this.archLeaf = _l;
    }

    /**
     * Check if this is a Circle event.
     *
     * @return true is the event has a beachline arch leaf associated.
     */
    public boolean isCircleEvent() {
        return this.getArchRef().isPresent();
    }

    /**
     * Get the site location for this event.
     *
     * @return the site of this event.
     */
    public Point getSite() {
        return this.site;
    }

    /**
     * Get the beachline arch leaf for this circle event.
     *
     * @return A leaf node of the beachline collapsed but this circle event.
     */
    public Optional<BeachNode> getArchRef() {
        return Optional.ofNullable(archLeaf);
    }
}
