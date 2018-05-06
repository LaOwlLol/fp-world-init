/**
 * The MIT License (MIT)

 Copyright (c) 2014 ajwerner

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in
 the Software without restriction, including without limitation the rights to
 use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 the Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fauxpas.fortunes.ajwerner;

import com.fauxpas.geometry.*;
import javafx.animation.AnimationTimer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Original class created by ajwerner on 12/23/13.
 * Modiftied by laowllol (Nate G.) in April 2018.
 */
public class Voronoi {

    ArrayList<Point> sites;
    private final ArrayList<VoronoiEdge> edgeList;
    private HashSet<BreakPoint> breakPoints;
    private TreeMap<ArcKey, CircleEvent> arcs;
    private TreeSet<Event> events;
    private Graph graph;
    private boolean finished;
    private double sweepLoc;
    private double padding;
    private double width;
    private double height;


    public Voronoi(double _width, double _height, double _padding) {
        // initialize data structures;
        edgeList = new ArrayList<VoronoiEdge>();
        events = new TreeSet<Event>();
        breakPoints = new HashSet<BreakPoint>();
        arcs = new TreeMap<ArcKey, CircleEvent>();
        graph = new Graph();
        this.width = _width;
        this.height = _height;
        this.padding = _padding;
    }

    public Graph getGraph() {
        return graph;
    }

    public ArrayList<VoronoiEdge> getEdgeList() {
        return edgeList;
    }

    /**
     * provide a list of sites for the graph
     *
     * Pre Condition unprocessed voronoi graph.
     * @param sites list of site to use.
     */
    public void setSites(ArrayList<Point> sites) {
        graph.setSites(sites);
    }

    /**
     * choose a list of sites on a plane.
     */
    public void generateSites(int count) {
        double low = padding;
        double x_max = width - padding;
        double y_max = height - padding;

        for (int i = 0; i < count; i++) {
            this.graph.addSite(new Point(ThreadLocalRandom.current().nextDouble(low, x_max),
                    ThreadLocalRandom.current().nextDouble(low, y_max)));
        }
    }

    /**
     * enqueue initial events from sites.
     */
    public void initEvents() {
        for (Point site : graph.getSites()) {
            events.add(new Event(site));
        }
    }

    /**
     * Get this Voronoi object ready for processing
     */
    public void initProcessing() {
        sweepLoc = height;
        finished = false;
    }

    public boolean hasNextEvent() {
        return !events.isEmpty();
    }

    public double getSweepLoc() {
        return sweepLoc;
    }

    /**
     * Add a list of points to the initial (no circle events detected) Events list.
     *
     * @param siteList a list of Points.
     */

    /**
     * Handle the next event.
     */
    public void processNextEvent() {
        if (hasNextEvent()) {
            Event cur = events.pollFirst();
            sweepLoc = cur.p.y();

            if (cur.getClass() == Event.class) {
                handleSiteEvent(cur);
            } else {
                CircleEvent ce = (CircleEvent) cur;
                handleCircleEvent(ce);
            }
        }
    }

    /**
     * Construct a animation timer for iterating over events until finished processing.
     * @return timer for animating graph processing.
     */
    public AnimationTimer getProcessAnimator() {
        return new AnimationTimer() {

            @Override
            public void handle(long now) {

                if (hasNextEvent()) {
                    processNextEvent();
                }
                else if (!isFinal()) {
                    finishBreakPoints();
                    this.stop();
                }

            }
        };
    }

    /**
     * Finalize the graph.
     */
    public void finishBreakPoints() {
        this.sweepLoc = -1000;
        for (BreakPoint bp : breakPoints) {
            bp.finish(graph);
        }
        finished = true;
    }

    public boolean isFinal() {
        return finished;
    }

    private void handleSiteEvent(Event cur) {
        // Deal with first point case
        if (arcs.size() == 0) {
            arcs.put(new Arc(cur.p, this), null);
            return;
        }

        // Find the arc above the site
        Map.Entry<ArcKey, CircleEvent> arcEntryAbove = arcs.floorEntry(new ArcQuery(cur.p));
        Arc arcAbove = (Arc) arcEntryAbove.getKey();

        // Deal with the degenerate case where the first two points are at the same y value
        if (arcs.size() == 0 && Double.compare(arcAbove.site.y(), cur.p.y()) == 0) {
            VoronoiEdge newEdge = new VoronoiEdge(arcAbove.site, cur.p);
            HalfEdge v = new HalfEdge();
            HalfEdge w = new HalfEdge();
            v.setTwin(w);
            w.setTwin(v);
            graph.addHalfEdge(v);
            graph.addHalfEdge(w);

            newEdge.setP1( new Point((cur.p.x() + arcAbove.site.x())/2, Double.POSITIVE_INFINITY) );

            Vertex vert = graph.getVertex(newEdge.getP1());
            graph.addVertex(vert);
            w.setOrigin(vert);
            vert.setIncidentHalfEdge(w);

            BreakPoint newBreak = new BreakPoint(arcAbove.site, cur.p, newEdge, v, w, false, this);
            breakPoints.add(newBreak);
            this.edgeList.add(newEdge);
            Arc arcLeft = new Arc(null, newBreak, this);
            Arc arcRight = new Arc(newBreak, null, this);
            arcs.remove(arcAbove);
            arcs.put(arcLeft, null);
            arcs.put(arcRight, null);
            return;
        }

        // Remove the circle event associated with this arc if there is one
        CircleEvent falseCE = arcEntryAbove.getValue();
        if (falseCE != null) {
            events.remove(falseCE);
        }

        BreakPoint breakL = arcAbove.left;
        BreakPoint breakR = arcAbove.right;
        VoronoiEdge newEdge = new VoronoiEdge(arcAbove.site, cur.p);
        this.edgeList.add(newEdge);
        HalfEdge v = new HalfEdge();
        HalfEdge w = new HalfEdge();
        v.setTwin(w);
        w.setTwin(v);
        graph.addHalfEdge(v);
        graph.addHalfEdge(w);

        BreakPoint newBreakL = new BreakPoint(arcAbove.site, cur.p, newEdge, v, w,true, this);
        BreakPoint newBreakR = new BreakPoint(cur.p, arcAbove.site, newEdge, v, w, false, this);
        breakPoints.add(newBreakL);
        breakPoints.add(newBreakR);

        Arc arcLeft = new Arc(breakL, newBreakL, this);
        Arc center = new Arc(newBreakL, newBreakR, this);
        Arc arcRight = new Arc(newBreakR, breakR, this);

        arcs.remove(arcAbove);
        arcs.put(arcLeft, null);
        arcs.put(center, null);
        arcs.put(arcRight, null);

        checkForCircleEvent(arcLeft);
        checkForCircleEvent(arcRight);
    }

    private void handleCircleEvent(CircleEvent ce) {
        Arc arcRight = (Arc) arcs.higherKey(ce.arc);
        Arc arcLeft = (Arc) arcs.lowerKey(ce.arc);
        if (arcRight != null) {
            CircleEvent falseCe = arcs.get(arcRight);
            if (falseCe != null) events.remove(falseCe);
            arcs.put(arcRight, null);
        }
        if (arcLeft != null) {
            CircleEvent falseCe = arcs.get(arcLeft);
            if (falseCe != null) events.remove(falseCe);
            arcs.put(arcLeft, null);
        }
        arcs.remove(ce.arc);

        ce.arc.left.finish(ce.vert, graph);
        ce.arc.right.finish(ce.vert, graph);

        breakPoints.remove(ce.arc.left);
        breakPoints.remove(ce.arc.right);

        VoronoiEdge e = new VoronoiEdge(ce.arc.left.s1, ce.arc.right.s2);
        edgeList.add(e);
        HalfEdge v = new HalfEdge();
        HalfEdge w = new HalfEdge();
        v.setTwin(w);
        w.setTwin(v);
        graph.addHalfEdge(v);
        graph.addHalfEdge(w);

        // Here we're trying to figure out if the org.ajwerner.voronoi.Voronoi vertex we've found is the left
        // or right point of the new edge.
        // If the edges being traces out by these two arcs take a right turn then we know
        // that the vertex is going to be above the current point
        boolean turnsLeft = Point.ccw(arcLeft.right.edgeBegin, ce.p, arcRight.left.edgeBegin) == 1;
        // So if it turns left, we know the next vertex will be below this vertex
        // so if it's below and the slope is negative then this vertex is the left point
        boolean isLeftPoint = (turnsLeft) ? (e.m < 0) : (e.m > 0);
        if (isLeftPoint) {
            e.setP1(ce.vert);
            Vertex vert = graph.getVertex(ce.vert);
            graph.addVertex(vert);
            w.setOrigin(vert);
            vert.setIncidentHalfEdge(w);
        }
        else {
            e.setP2(ce.vert);
            Vertex vert = graph.getVertex(ce.vert);
            graph.addVertex(vert);
            v.setOrigin(vert);
            vert.setIncidentHalfEdge(v);
        }

        BreakPoint newBP = new BreakPoint(ce.arc.left.s1, ce.arc.right.s2, e, w, v, !isLeftPoint, this);
        breakPoints.add(newBP);

        arcRight.left = newBP;
        arcLeft.right = newBP;

        checkForCircleEvent(arcLeft);
        checkForCircleEvent(arcRight);
    }

    private void checkForCircleEvent(Arc a) {
        Point circleCenter = a.checkCircle();
        if (circleCenter != null) {
            double radius = a.site.distanceTo(circleCenter);
            Point circleEventPoint = new Point(circleCenter.x(), circleCenter.y() - radius);
            CircleEvent ce = new CircleEvent(a, circleEventPoint, circleCenter);
            arcs.put(a, ce);
            events.add(ce);
        }
    }

}
