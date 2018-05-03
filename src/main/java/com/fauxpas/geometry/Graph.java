package com.fauxpas.geometry;

import java.util.ArrayList;
import java.util.LinkedHashSet;


public class Graph {

    private ArrayList<Point> voronoiSites;
    private LinkedHashSet<Vertex> vertices;
    private LinkedHashSet<Face> faces;
    private LinkedHashSet<HalfEdge> edges;

    public LinkedHashSet<Vertex> getVertices() {
        return vertices;
    }

    public LinkedHashSet<Face>  getFaces() {
        return faces;
    }

    public LinkedHashSet<HalfEdge> getEdges() {
        return edges;
    }

    //TODO there has got to be a better way to to this.
    public LinkedHashSet<HalfEdge> neighboringHalfEdges(Vertex v){
        LinkedHashSet<HalfEdge> results = new LinkedHashSet<>();
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

    /**
     * Get the nth vertex added into the graph.
     *
     * @param n index of the vertex to return.
     * @return nth vertex inserted into the graph, or null if n is out of range;
     */
    public Vertex getVertex(int n) {
        int i = 0;
        for (Vertex v: getVertices()) {
            if (i == n) {
                return v;
            }
            i++;
        }

        return null;
    }

    public Vertex getVertex(Point p) {
        Vertex v = containsPointAsVertex(p);
        if (v != null) {
            return v;
        }
        return new Vertex(p);
    }

    public boolean isVertex(Point p) {
        return containsPointAsVertex(p) != null;
    }

    private Vertex containsPointAsVertex(Point p) {
        for (Vertex v: getVertices()) {
            if (p.compareTo(v.getCoordinates()) == 0) {
                return v;
            }
        }
        return null;
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
        this.vertices = new LinkedHashSet<>();
        this.faces = new LinkedHashSet<>();
        this.edges = new LinkedHashSet<>();
        this.voronoiSites = new ArrayList<>();
    }

    public Graph(LinkedHashSet<Vertex> _vertices, LinkedHashSet<Face> _faces, LinkedHashSet<HalfEdge> _edges, ArrayList<Point> _sites) {
        if (_vertices != null) {
            this.vertices = _vertices;
        }
        else {
            this.vertices = new LinkedHashSet<>();
        }
        if (_faces != null) {
            this.faces = _faces;
        }
        else {
            this.faces = new LinkedHashSet<>();
        }
        if (_edges != null) {
            this.edges = _edges;
        }
        else {
            this.edges = new LinkedHashSet<>();
        }
        if (_sites != null) {
            this.voronoiSites = _sites;
        }
        else {
            this.voronoiSites = new ArrayList<>();
        }
    }

}
