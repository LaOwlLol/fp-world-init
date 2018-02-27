package com.fauxpas.geometry;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyList {

    private GNode root;
    private List<GNode> adjacentNodes;

    public AdjacencyList(GNode _v) {
        this.root = _v;
        this.adjacentNodes = new ArrayList<GNode>();
    }

    public AdjacencyList(GNode _v, List<GNode> _e) {
        this.root = _v;
        this.adjacentNodes = new ArrayList<GNode>();
        this.adjacentNodes.addAll(_e);
    }

    /**
    Adds a node (_toNode) to the adjacentNodes list, if the following is true:
     - _toNode is not contained in this adjacentNodes list (only one node from A to B).
     
    @param _toNode an adjacent node on a graph.
    */
    public void addAdjacency(GNode _toNode) {
        if (!this.isAdjacent(_toNode)) {
            this.adjacentNodes.add(_toNode);
        }
    }


    public void removeAdjacentNode(GNode _toNode) {
        if (this.isAdjacent(_toNode)) {
            this.adjacentNodes.remove(_toNode);
        }
    }

    public void removeAdjacentNodes() {
        this.adjacentNodes = new ArrayList<GNode>();
    }

    public boolean isAdjacent(GNode _toNode) {
        return this.adjacentNodes.contains(_toNode);
    }

    public boolean isRoot(GNode _v) {
        return root.isSameNode(_v);
    }
}
