package com.fauxpas.geometry;

import java.util.ArrayList;
import java.util.HashSet;

public class Graph {

    private ArrayList<Point> voronoiSites;
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

    //TODO there has got to be a better way to to this.
    public HashSet<HalfEdge> neighboringHalfEdges(Vertex v){
        HashSet<HalfEdge> results = new HashSet<>();
        for (HalfEdge h: edges) {
            if (h.Origin().equals(v) || h.Destination().equals(v)) {
                results.add(h);
            }
        }
        return results;
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

    public ArrayList<Point> getSites() {
        return voronoiSites;
    }

    public void setSites(ArrayList<Point> voronoiSites) {
        this.voronoiSites = voronoiSites;
    }

    public void addSite(Point point) {
        voronoiSites.add(point);
    }

    public Graph() {
        this.vertices = new HashSet<>();
        this.faces = new HashSet<>();
        this.edges = new HashSet<>();
        this.voronoiSites = new ArrayList<>();
    }

    public Graph(HashSet<Vertex> _vertices, HashSet<Face> _faces, HashSet<HalfEdge> _edges, ArrayList<Point> _sites) {
        if (_vertices != null) {
            this.vertices = _vertices;
        }
        else {
            this.vertices = new HashSet<>();
        }
        if (_faces != null) {
            this.faces = _faces;
        }
        else {
            this.faces = new HashSet<>();
        }
        if (_edges != null) {
            this.edges = _edges;
        }
        else {
            this.edges = new HashSet<>();
        }
        if (_sites != null) {
            this.voronoiSites = _sites;
        }
        else {
            this.voronoiSites = new ArrayList<>();
        }
    }

}
