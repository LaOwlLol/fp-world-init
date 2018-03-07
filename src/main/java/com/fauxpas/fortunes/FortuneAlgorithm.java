package com.fauxpas.fortunes;

import com.fauxpas.geometry.GNode;
import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.Point;
import com.fauxpas.geometry.AdjacencyList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

public class FortuneAlgorithm {

    private Graph voronoi;
    private BeachNode beachline;
    private PriorityQueue<FortuneEvent> events;
    private Point L;

    public FortuneAlgorithm(int _pointCount, double _width, double _height) {
        this.voronoi = new Graph();
        this.beachline = null;
        this.events = new PriorityQueue<FortuneEvent>(_pointCount, FortuneHelpers::compareYNatural);

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
     * post: beach line initialized with a new leaf for the current L event.
     *
     * Note this method implements HandleSiteEvent(Pi) step 1 of p158 Computational
     * Geometry Algorithms Applications Berg et al,  with one exception it will
     * not break out of the HandleSiteEvent subrutine. Check return value when
     * calling and break out when true.
     *
     * @return whether a new tree was created with the current L point.
     */
    private boolean ifNewTree() {
        if (this.beachline == null) {
            this.beachline = new BeachNode();
            this.beachline.setSite(this.L);
            return true;
        }
        else {
            return false;
        }
    }

    private void insertBeachArch( BeachNode _b ) {
        this.beachline = insertArch(this.beachline, _b);
    }

    private BeachNode insertArch(BeachNode _root, BeachNode _b) {

        if (_root == null) {
            _root = _b;
            return _root;
        }

        if ( _b.getX(L) < _root.getX(L) ) {
            _root.setLeft(insertArch(_root.getLeft(), _b));
        }
        else {
            _root.setRight(insertArch(_root.getRight(), _b));
        }

        return _root;
    }

    private void removeBeachArch( BeachNode _b) {
        this.beachline = removeArch(this.beachline, _b);
    }

    private BeachNode removeArch(BeachNode _root, BeachNode _b) {
        if (_root == null) {
            return null;
        }
        if (_root.isLeaf()) {
            //is this the arch to delete?
            if (_root.getSite().effectivelyEqual(_b.getSite(), 0.01)) {
                //remove circle event if it has one.
                removeCircleEvent(_root);
                return null;
            }
            return _root;
        }
        else {
            //then it's a breakpoint or parent node.
            if (_root.getLeft() == null || _root.getRight() == null) {
                //i think this is a degenerate case..
                // all non leafs should have both left and right set
                System.out.println("degenerate bug. on remove non leaf has left or right null...");
                System.out.println(" returning this node un modified for now.");
                return _root;
            }
            // simple case left and right are not breakpoints
            //follow normal BST procedure searching on x values.
            if (_root.getSite().compareX(_b.getSite()) < 0) {
                _root.setLeft(removeArch(_root.getLeft(), _b));
                //if we delete the left branch return the right.
                if (_root.getLeft() == null) {
                    return _root.getRight();
                }
                return _root;
             }
             else {
                _root.setRight(removeArch(_root.getRight(), _b));
                //if we delete the left branch return the right.
                if (_root.getRight() == null) {
                    return _root.getLeft();
                }
                return _root;
            }
        }
    }

    private Optional<BeachNode> searchforArchAbove(BeachNode _root, BeachNode _q ) {
        if (_root == null) {
            return Optional.ofNullable(_root);
        }
        if (_root.isLeaf()) {
            if (FortuneHelpers.isQAboveParabolaPL(_q.getSite(), _root.getSite(), this.L.y())) {
                return Optional.ofNullable(_root);
            }
        }

        if (_root.getX(L) < _q.getX(L)) {
            return searchforArchAbove(_root.getLeft(), _q);
        }
        else {
            return searchforArchAbove(_root.getRight(), _q);
        }
    }

    private BeachNode splitLeafForNewArch( BeachNode _oldArch, BeachNode _newArch ) {
        BeachNode newParentBreak = new BeachNode();
        BeachNode newChildBreak = new BeachNode();

        BeachNode oldCopy = new BeachNode();
        oldCopy.setSite(_oldArch.getSite());

        newChildBreak.setLeft(_newArch);
        newChildBreak.setRight(oldCopy);
        newChildBreak.setSite(_oldArch.getSite());
        newChildBreak.setFutureEdge(this.getHalfEdge());
        newParentBreak.setLeft(_oldArch);
        newParentBreak.setRight(newChildBreak);
        newParentBreak.setSite(_newArch.getSite());
        newParentBreak.setFutureEdge(this.getHalfEdge());

        checkForCircleEvent(newParentBreak);
        checkForCircleEvent(newChildBreak);

        return newParentBreak;
    }

    private AdjacencyList getHalfEdge() {
        //GNode vertex = new GNode(_v);
        return new AdjacencyList();
    }

    private boolean checkForCircleEvent(BeachNode _p) {
        if (_p == null) {
            return false;
        }
        if (_p.isLeaf()) {
            return false;
        }
        if (_p.getLeft().getSite().effectivelyEqual(_p.getRight().getSite(), 0.01)) {
            return false;
        }
        Optional<Point> s = _p.getBreakPoint(L);
        if (!s.isPresent()) {
            return false;
        }
        else {
            if ( s.get().y() + s.get().euclideanDistance(_p.getSite()) < L.y() ) {

                FortuneEvent ce = new FortuneEvent( new Point (s.get().x(),
                        s.get().y() + s.get().euclideanDistance(_p.getSite())) );
                ce.setArchLeaf(_p);
                _p.setCircleEvent(ce);
                events.add(ce);

                return true;
            }
            else {
                return false;
            }
        }

    }

    private void removeCircleEvent(BeachNode _b) {
        if (!_b.isLeaf()) {
            return;
        }
        _b.getCircle().ifPresent((ce) -> {
            events.remove(ce);
        });
    }

}
