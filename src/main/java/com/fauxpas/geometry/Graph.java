package com.fauxpas.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Graph {

	private List<AdjacencyList> data;

	public Graph() {
		this.data = new ArrayList<AdjacencyList>();
	}

	public void addVertex(GNode _v) {
		if (!getAdjacencyList(_v).isPresent()) {
			this.addVertexWithEdges(new AdjacencyList(_v));
		}		
	}

	public void addEdge(GNode _root, GNode _v) {

		if (getAdjacencyList(_v).isPresent()) {
			getAdjacencyList(_v).ifPresent( (el) -> {
				if (!el.isAdjacent(_root)) {
					ifHasAdjListAddNode(_root, _v);
				}
			});
		}
		else {
			ifHasAdjListAddNode(_root, _v);
		}
		
	}

	public void addVertexWithEdges(AdjacencyList _e) {
		this.data.add(_e);
	}

	public List<GNode> getVertices() {
		ArrayList<GNode> verticies = new ArrayList<GNode>();
		for (AdjacencyList _e: data) {
			verticies.add(_e.getRoot());
		}

		return verticies;
	} 

	public List<List<GNode>> getEdges() {
		ArrayList<List<GNode>> edgesList = new ArrayList<List<GNode>>();
		for (AdjacencyList aList: data) {
			for (GNode _v: aList.getAdjacencies()) {
				ArrayList<GNode> edge  = new ArrayList<GNode>();
				edge.add(aList.getRoot());
				edge.add(_v);
				edgesList.add(edge);
			}
		}

		return edgesList;
	}

	private void ifHasAdjListAddNode(GNode _root, GNode _v) {
		getAdjacencyList(_root).ifPresent( (el) -> {
			el.addAdjacency(_v);
		});
	}

	private Optional<AdjacencyList> getAdjacencyList(GNode _root) {
		for (AdjacencyList _e: data) {
			if (_e.isRoot(_root)) {
				return Optional.of(_e);
			}
		}

		return Optional.empty();
	}

}