package com.fauxpas.geometry;

import com.sun.javafx.geom.Edge;

import java.util.ArrayList;
import java.util.List;

public class EdgeList {

    private GNode vertex;
    private List<GNode> edges;

    public EdgeList(GNode _v) {
        this.vertex = _v;
        this.edges = new ArrayList<GNode>();
    }

    public EdgeList(GNode _v, List<GNode> _e) {
        this.vertex = _v;
        this.edges = new ArrayList<GNode>();
        this.edges.addAll(_e);
    }

    public void addEgde(GNode _toNode) {
        this.edges.add(_toNode);
    }

    public void removeEdge(GNode _toNode) {
        this.edges.remove(_toNode);
    }

    public void removeEdges() {
        this.edges = new ArrayList<GNode>();
    }

    public boolean isAdjacent(GNode _toNode) {
        return this.edges.contains(_toNode);
    }

    public boolean isVertex(GNode _v) {
        return vertex.isSameNode(_v);
    }
}
