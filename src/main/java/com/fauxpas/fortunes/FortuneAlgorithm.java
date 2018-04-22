package com.fauxpas.fortunes;

import com.fauxpas.geometry.GNode;
import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.Point;
import com.fauxpas.geometry.AdjacencyList;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FortuneAlgorithm {

    private Graph voronoi;
    private Graph sites;
    private Graph circles;
    private BeachNode beachline;
    private PriorityQueue<FortuneEvent> events;
    private FortuneEvent L;
    private double sweep;

    public FortuneAlgorithm(int _pointCount, double _width, double _height) {
        this.voronoi = new Graph();
        this.sites = new Graph();
        this.circles = new Graph();
        this.beachline = null;
        this.events = new PriorityQueue<FortuneEvent>(_pointCount, FortuneHelpers::compareYNatural);
        int lowD = 100;
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

    public List<GNode> getSites() {
        return this.sites.getVertices();
    }

    public List<GNode> getCircles() {
        return this.circles.getVertices();
    }

    public List<GNode> getVertices() {
        return this.voronoi.getVertices();
    }

    public List<List<GNode>> getEdges() {
        return this.voronoi.getEdges();
    }

    public double getSweep() {
        return this.sweep;
    }

    /*****************************************************************************/
    /*                                Tree Methods                               */
    /*****************************************************************************/

    public void processGraph() {
        while (!events.isEmpty()) {
           processNextEvent();
        }
    }

    public void processNextEvent() {
        if (!events.isEmpty()) {
            L = events.poll();
            this.sweep = L.getSite().y();
            sites.addVertex(new GNode(L.getSite()));
            if (L.getArchRef().isPresent()) {

                L.getArchRef().ifPresent((a) -> {
                    System.out.println("Circle event.");
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

    private void removeBeachArch( BeachNode _b) {
        this.beachline = removeArch(this.beachline, _b);
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

    private BeachNode removeArch(BeachNode _root, BeachNode _b) {
        if (_root == null) {
            return _root;
        }
        if (_root.isLeaf()) {
            if (_root.getSite().effectivelyEqual(_b.getSite(), 1)) {

                finishVoronoiEdgeWithVertex(_root);
                removeCircleEvent(_root.getRight());
                removeCircleEvent(_root.getLeft());
                return null;
            }
            else {
                return (_root);
            }
        }
        else {
            if (_root.getSite().compareX(_b.getSite()) < 0) {
                _root.setLeft(removeArch(_root.getLeft(), _b));
                //if we deleted the left branch we delete the circle event on right branch and return it.
                if (_root.getLeft() == null) {
                    return _root.getRight();
                }
                return _root;
            }
            else {
                _root.setRight(removeArch(_root.getRight(), _b));
                //if we delete the right branch we delete the circle event on left branch and return it.
                if (_root.getRight() == null) {
                    return _root.getLeft();
                }
                return _root;
            }
        }
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

    private AdjacencyList getNewEdge(Point _v) {
        GNode vertex = new GNode(_v);
        AdjacencyList edges = new AdjacencyList(vertex);
        voronoi.addVertexWithEdges(edges);
        return edges;
    }

    private void finishVoronoiEdgeWithVertex(BeachNode _b)  {
        voronoi.addVertex(_b.getEdgeEnd());
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
            _j.setEdgeEnd(new GNode(s));
            this.circles.addVertex(_j.getEdgeEnd());
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
            _b.setCircleEvent(null);
        });
    }

}
