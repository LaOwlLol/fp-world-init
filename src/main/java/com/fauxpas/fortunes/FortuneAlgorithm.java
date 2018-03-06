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

    private void insert( BeachNode _addition ) {
        this.beachline = insertArch(this.beachline, _addition);
    }

    private BeachNode insertArch(BeachNode _root, BeachNode _addition) {

        if (_root == null) {
            _root = _addition;
            return _root;
        }

        if ( _addition.getX(L) < _root.getX(L) ) {
            _root.setLeft(insertArch(_root.getLeft(), _addition));
        }
        else {
            _root.setRight(insertArch(_root.getRight(), _addition));
        }

        return _root;
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
        newChildBreak.setFutureEdge(this.getHalfEdge());
        newParentBreak.setLeft(_oldArch);
        newParentBreak.setRight(newChildBreak);
        newParentBreak.setFutureEdge(this.getHalfEdge());

        return newParentBreak;
    }

    private AdjacencyList getHalfEdge() {
        //GNode vertex = new GNode(_v);
        return new AdjacencyList();
    }

}
