package com.fauxpas.fortunes;

import com.fauxpas.geometry.GNode;
import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.Point;
import com.fauxpas.geometry.AdjacencyList;

import java.util.List;
import java.util.PriorityQueue;

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
                    removeBeachArch(a.getSite());
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
        this.beachline = insertArch(this.beachline, _b);
    }

    private void removeBeachArch( Point _b) {
        this.beachline = removeArch(null, this.beachline, _b);
    }

    private BeachNode insertArch(BeachNode _root, BeachNode _b) {
        if (isArchAbove(_root, _b, L.getSite().y())) {
            _root = splitArch(_root, _b);
            return _root;
        }

        if (_b.getSite().x() < _root.getSite().x()) {
            if (_root.getLeft() != null) {
                _root.setLeft(insertArch(_root.getLeft(), _b));
            }
            else {
                _root = splitArch(_root, _b);
                return _root;
            }
        } else {
            if (_root.getRight() != null) {
                _root.setRight(insertArch(_root.getRight(), _b));
            }
            else {
                _root = splitArch(_root, _b);
                return _root;
            }
        }


        return _root;
    }

    private BeachNode removeArch(BeachNode _parent, BeachNode _root, Point _b) {
        if (_root == null) {
            return null;
        }
        if (_root.isLeaf()) {
            //is this the arch to delete?
            if (_root.getSite().effectivelyEqual(_b, 0.01)) {
                //remove circle event if it has one.
                finishVoronoiEdgeWithVertex(_parent ,_root, _b);
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
            if (_root.getSite().compareX(_b) < 0) {
                _root.setLeft(removeArch(_root, _root.getLeft(), _b));
                //if we deleted the left branch we delete the circle event on right branch and return it.
                if (_root.getLeft() == null) {
                    removeCircleEvent(_root.getRight());
                    return _root.getRight();
                }
                return _root;
            }
            else {
                _root.setRight(removeArch(_root, _root.getRight(), _b));
                //if we delete the right branch we delete the circle event on left branch and return it.
                if (_root.getRight() == null) {
                    removeCircleEvent(_root.getLeft());
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
        if (FortuneHelpers.isQAboveParabolaPL(_q.getSite(), _root.getSite(), _l)) {
            return true;
        }

        return false;
    }

    private BeachNode splitArch( BeachNode _oldArch, BeachNode _newArch ) {
        BeachNode newParentBreak = new BeachNode();
        BeachNode newChildBreak = new BeachNode();

        BeachNode oldCopy = new BeachNode();
        oldCopy.setSite(_oldArch.getSite());

        newChildBreak.setLeft(_newArch);
        newChildBreak.setRight(oldCopy);
        newChildBreak.setSite(FortuneHelpers.perpendicular(_newArch.getSite(), oldCopy.getSite()));
        newChildBreak.setEdgeEnd(new GNode(newChildBreak.getSite()));
        voronoi.addVertex(newChildBreak.getEdgeEnd());

        newParentBreak.setLeft(_oldArch);
        newParentBreak.setRight(newChildBreak);
        newParentBreak.setSite( FortuneHelpers.perpendicular(_oldArch.getSite(), _newArch.getSite()) );
        newParentBreak.setEdgeEnd(new GNode(newParentBreak.getSite()));
        voronoi.addVertex(newParentBreak.getEdgeEnd());

        checkForCircleEvent(newParentBreak);
        checkForCircleEvent(newChildBreak);

        return newParentBreak;
    }

    private AdjacencyList getNewEdge(Point _v) {
        GNode vertex = new GNode(_v);
        AdjacencyList edges = new AdjacencyList(vertex);
        voronoi.addVertexWithEdges(edges);
        return edges;
    }

    private void finishVoronoiEdgeWithVertex(BeachNode _parent, BeachNode _b, Point _l)  {
        if (_parent.getEdgeEnd() != null) {
            voronoi.addEdge(_b.getEdgeEnd(),
                    new GNode(FortuneHelpers.getCenterOfCircumCircle(
                            _b.getLeft().getSite(), _b.getRight().getSite(), _l)));
            System.out.println("Edge added.");
        }
        else {
            System.out.println("Collapsing edgeEnd was null.");
        }
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
        
        Point s = FortuneHelpers.getCenterOfCircumCircle(_p.getLeft().getSite(), _p.getRight().getSite(), L.getSite());
        if ( s.y() + s.euclideanDistance(_p.getSite()) > L.getSite().y() ) {

            FortuneEvent ce = new FortuneEvent( new Point (s.x(),
                    s.y() + s.euclideanDistance(_p.getSite())) );
            ce.setArchLeaf(_p);
            _p.setCircleEvent(ce);
            events.add(ce);
            circles.addVertex(new GNode(s));
            //System.out.println("Adding circle event.");

            return true;
        }
        else {
            return false;
        }

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
