package com.fauxpas.fortunes;

import com.fauxpas.geometry.GNode;
import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.Point;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

public class FortuneAlgorithm {

    private Graph voronoi;
    private BeachNode beachline;
    private PriorityQueue<FortuneEvent> events;
    private PriorityQueue<FortuneEvent> processed;
    private Point L;

    public FortuneAlgorithm(int _pointCount, double _width, double _height) {
        this.voronoi = new Graph();
        this.beachline = null;
        this.events = new PriorityQueue<FortuneEvent>(_pointCount, FortuneHelpers::compareYNatural);
        this.processed = new PriorityQueue<FortuneEvent>(_pointCount, FortuneHelpers::compareYReverse);

        for (int i = 0; i < _pointCount; ++i) {
            this.events.offer(new FortuneEvent(new Point(Math.random() * _width, Math.random() * _height)));
        }
    }

    public void printEvents() {
        PriorityQueue<FortuneEvent> copy = new PriorityQueue<FortuneEvent>(50, FortuneHelpers::compareYNatural);

        copy.addAll(events);

        while (!copy.isEmpty()) {
            FortuneEvent e = copy.poll();
            System.out.println(e.getSite().x()+", "+e.getSite().y());
        }
    }

    public List<GNode> getVertices() {
        return this.voronoi.getVertices();
    }

    public List<List<GNode>> getEdges() {
        return this.voronoi.getEdges();
    }


    /*****************************************************************************/
    /*                                Tree Methods                               */
    /*****************************************************************************/

    /**Pre: L must be set.
     * post: beach line initialized with a new leafNode for the current L event.
     *
     * Note this directly implements HandleSiteEvent(Pi) step 1 of p158 Computational
     * Geometry Algorithms Applications Berg et al.  With one exception it will
     * not break out of the HandleSiteEvent subrutine. Check return value when
     * calling and break out when true.
     *
     * @return whether a new tree was created with the current L point.
     */
    private boolean ifNewTree() {
        if (this.beachline == null) {
            this.beachline = new BeachLeaf(this.L);

            return true;
        }
        else {
            return false;
        }
    }

}
