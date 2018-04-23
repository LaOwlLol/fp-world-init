package com.fauxpas.fortunes;


import com.fauxpas.geometry.Point;
import com.fauxpas.geometry.AdjacencyList;
import com.fauxpas.geometry.topology.DCELGraph;
import com.fauxpas.geometry.topology.HalfEdge;
import com.fauxpas.geometry.topology.Vertex;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class FortuneAlgorithm {

    private DCELGraph voronoi;
    private DCELGraph sites;
    private DCELGraph circles;
    private BeachNode beachline;
    private PriorityQueue<FortuneEvent> events;
    private FortuneEvent L;
    private double sweep;

    public FortuneAlgorithm(int _pointCount, double _width, double _height, int padding) {
        this.voronoi = new DCELGraph();
        this.sites = new DCELGraph();
        this.circles = new DCELGraph();
        this.beachline = null;
        this.events = new PriorityQueue<FortuneEvent>(_pointCount, FortuneHelpers::compareYNatural);
        int lowD = padding;
        int highW = (int) Math.floor(_width - lowD);
        int highH = (int) Math.floor(_height - lowD);


        for (int i = 0; i < _pointCount; ++i) {
            this.events.offer(new FortuneEvent(new Point( ThreadLocalRandom.current().nextInt(lowD, highW),
                    ThreadLocalRandom.current().nextInt(lowD, highH))));
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

    public Set<Vertex> getSites() {
        return this.sites.getVertices();
    }

    public Set<Vertex> getCircles() {
        return this.circles.getVertices();
    }

    public Set<Vertex> getVertices() {
        return this.voronoi.getVertices();
    }

    public Set<HalfEdge> getEdges() {
        return this.voronoi.getEdges();
    }

    public double getSweep() {
        return this.sweep;
    }

    /*****************************************************************************/
    /*                                Tree Methods                               */
    /*****************************************************************************/

    private boolean isBalanced(BeachNode _b) {
        return (beachHeight(_b) > -1);
    }

    private int beachHeight(BeachNode _b) {
        if (_b == null) {
            return 0;
        }

        int h1 = beachHeight(_b.getLeft());
        int h2 = beachHeight(_b.getRight());

        if (h1 == -1 || h2 == -1) {
            return -1;
        }
        if (Math.abs(h1-h2) > 1) {
            return -1;
        }

        return Math.max(h1, h2) + 1;
    }

    public void processGraph() {
        while (!events.isEmpty()) {
           processNextEvent();
        }
    }

    public void processNextEvent() {
        if (!events.isEmpty()) {
            L = events.poll();
            this.sweep = L.getSite().y();
            sites.addVertex(new Vertex(L.getSite()));
            if (L.getArchRef().isPresent()) {

                L.getArchRef().ifPresent((a) -> {
                    System.out.println("Circle event.");
                    this.voronoi.addVertex(new Vertex(L.getCircleSite()));
                    removeBeachArch(a);
                });

            } else {
                System.out.println("Site event.");
                BeachNode _b = new BeachNode();
                _b.setSite(L.getSite());

                if (this.beachline == null) {
                    this.beachline = _b;
                } else {
                    insertBeachArch(_b);
                }
            }
        }
    }

    private void insertBeachArch( BeachNode _b ) {
        this.beachline = insertArch(null, this.beachline, _b);

    }

    private BeachNode insertArch(BeachNode _parent, BeachNode _root, BeachNode _b) {
        if (isArchAbove(_root, _b, L.getSite().y())) {
            removeCircleEvent(_root);
            _root = splitArch(_root, _b);
            checkForCircleEvent(_b, _root.getLeft(), getLeftMostArch(_parent));
            checkForCircleEvent(getRightMostArch(_parent), _root.getLeft(), _b);
            return _root;
        }

        if (_b.getSite().x() < _root.getSite().x()) {
            if (_root.getLeft() != null) {
                _root.setLeft(insertArch(_root, _root.getLeft(), _b));
            }
            else {
                removeCircleEvent(_root);
                _root = splitArch(_root, _b);
                checkForCircleEvent(_b, _root.getLeft(), getLeftMostArch(_parent));
                checkForCircleEvent(getRightMostArch(_parent), _root.getLeft(), _b);
                return _root;
            }
        } else {
            if (_root.getRight() != null) {
                _root.setRight(insertArch(_root, _root.getRight(), _b));
            }
            else {
                removeCircleEvent(_root);
                _root = splitArch(_root, _b);
                checkForCircleEvent(_b, _root.getLeft(), getLeftMostArch(_parent));
                checkForCircleEvent(getRightMostArch(_parent), _root.getLeft(), _b);
                return _root;
            }
        }
        return _root;
    }

    private void removeBeachArch( BeachNode _b) {
        System.out.println("searching for "+_b.getSite().toString());
        this.beachline = removeArch(this.beachline, _b);
    }

    private BeachNode removeArch(BeachNode _root, BeachNode _b) {
        /* Base Case: If the tree is empty */
        if (_root == null)  return _root;

        // check this is a parent.
        if (!_root.isLeaf()) {
            /* Otherwise, recur down the tree */
            if (_b.getSite().x() < _root.getSite().x()) {
                if (_root.getLeft() != null) {
                    _root.setLeft(removeArch(_root.getLeft(), _b));
                    if (_root.getLeft() == null) {
                        removeCircleEvent(getLeftMostArch(_root));
                        removeCircleEvent(getRightMostArch(_root));
                        finishVoronoiEdgeWithVertex(_root, _b);
                        return _root.getRight();
                    }
                }
                else {
                    return _root;
                }
            }
            else {
                if (_root.getRight() != null) {
                    _root.setRight(removeArch(_root.getRight(), _b));
                    if (_root.getRight() == null) {
                        removeCircleEvent(getLeftMostArch(_root));
                        removeCircleEvent(getRightMostArch(_root));
                        finishVoronoiEdgeWithVertex(_root, _b);
                        return _root.getLeft();
                    }
                }
                else {
                    return _root;
                }
            }
        }

            // if key is same as root's key, then This is the node
            // to be deleted
        else if (_root.isLeaf() && _b.getSite().effectivelyEqual(_root.getSite(), 0.01))
        {
           return null;
        }

        return _root;
    }

    private boolean isArchAbove(BeachNode _root, BeachNode _q, double _l ) {
        if (_root == null) {
           return false;
        }
        if (!_root.isLeaf()) {
            return false;
        }
        if (FortuneHelpers.isQAboveParabolaPL(_q.getSite(), _root.getSite(), _l)) {
            return true;
        }

        return false;
    }

    private BeachNode getLeftMostArch(BeachNode _root) {
        if (_root == null) {
            return null;
        }
        if (_root.isLeaf()) {
            return _root;
        }
        else {
            if (_root.getLeft() == null) {
                return getLeftMostArch(_root.getRight());
            }
            else {
                return getLeftMostArch(_root.getLeft());
            }
        }
    }

    private BeachNode getRightMostArch(BeachNode _root) {
        if (_root == null) {
            return _root;
        }
        if (_root.isLeaf()) {
            return _root;
        }
        else {
            if (_root.getRight() == null) {
                return getRightMostArch(_root.getLeft());
            }
            else {
                return getRightMostArch(_root.getRight());
            }
        }
    }

    private BeachNode splitArch( BeachNode _oldArch, BeachNode _newArch ) {
        BeachNode newParentBreak = new BeachNode();
        BeachNode newChildBreak = new BeachNode();

        BeachNode oldCopy = new BeachNode();
        oldCopy.setSite(_oldArch.getSite());

        newChildBreak.setLeft(_newArch);
        newChildBreak.setRight(oldCopy);
        newChildBreak.setSite(FortuneHelpers.perpendicular(_newArch.getSite(), oldCopy.getSite()));

        newParentBreak.setLeft(_oldArch);
        newParentBreak.setRight(newChildBreak);
        newParentBreak.setSite( FortuneHelpers.perpendicular(_oldArch.getSite(), _newArch.getSite()) );


        return newParentBreak;
    }

    private void finishVoronoiEdgeWithVertex(BeachNode _parent, BeachNode _b)  {

        System.out.println("HalfEdge added.");

    }

    private boolean checkForCircleEvent(BeachNode _i, BeachNode _j, BeachNode _k) {
        if (_i == null || _j == null || _k == null) {
            return false;
        }
        if (_i.getSite().effectivelyEqual(_k.getSite(), 0.01)) {
            return false;
        }
        if (_i.getSite().effectivelyEqual(_j.getSite(), 0.01)) {
            return false;
        }
        if (_k.getSite().effectivelyEqual(_j.getSite(), 0.01)) {
            return false;
        }

        Point s = FortuneHelpers.getCenterOfCircumCircle(_i.getSite(), _j.getSite(), _k.getSite());
        if ( s.y() + s.euclideanDistance(_j.getSite()) > L.getSite().y() ) {

            FortuneEvent ce = new FortuneEvent( new Point (s.x(),
                    s.y() + s.euclideanDistance(_j.getSite())) );
            ce.setArchLeaf(_j);
            ce.setCircleSite(s);
            _j.setHalfEdge(new HalfEdge(new Vertex(s)));
            this.circles.addVertex(new Vertex(ce.getSite()));
            //_j.setCircleEvent(ce);
            _i.setCircleEvent(ce);
            _k.setCircleEvent(ce);
            events.add(ce);

            return true;
        }

        return false;
    }

    private void removeCircleEvent(BeachNode _b) {
        if (_b == null) {
            return;
        }
        if (!_b.isLeaf()) {
            return;
        }
        _b.getCircle().ifPresent((ce) -> {
            events.remove(ce);
            System.out.println("removed ce from "+_b.getSite().toString());
            _b.setCircleEvent(null);
        });
    }

}
