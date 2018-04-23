package com.fauxpas.geometry;

import java.util.HashSet;

public class Graph {

    private HashSet<Vertex> vertices;
    private HashSet<Face> faces;
    private HashSet<HalfEdge> edges;

    public HashSet<Vertex> getVertices() {
        return vertices;
    }

    public HashSet<Face>  getFaces() {
        return faces;
    }

    public HashSet<HalfEdge> getEdges() {
        return edges;
    }

    public void addVertex(Vertex v) {
        this.vertices.add(v);
    }

    public void addHalfEdge(HalfEdge e) {
        this.edges.add(e);
    }

    public void addFace(Face f) {
        this.faces.add(f);
    }

    public Graph() {
        this.vertices = new HashSet<>();
        this.faces = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public Graph(HashSet<Vertex> _vertices, HashSet<Face> _faces, HashSet<HalfEdge> _edges) {
        if (_vertices != null) {
            this.vertices = _vertices;
        }
        if (_faces != null) {
            this.faces = _faces;
        }
        if (_edges != null) {
            this.edges = _edges;
        }
    }
}
