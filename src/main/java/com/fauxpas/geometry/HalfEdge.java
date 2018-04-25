package com.fauxpas.geometry;

public class HalfEdge {

    private Vertex origin;
    private HalfEdge twin;
    private HalfEdge next;
    private HalfEdge prev;
    private Face incidentFace;

    public Vertex Origin() {
        return origin;
    }

    public void setOrigin(Vertex origin) {
        this.origin = origin;
    }

    public Vertex Destination() {
        if (this.twin != null) {
            return this.twin.Origin();
        }
        else {
            return null;
        }
    }

    public HalfEdge Twin() {
        return twin;
    }

    public void setTwin(HalfEdge twin) {
        this.twin = twin;
    }

    public HalfEdge Next() {
        return next;
    }

    public void setNext(HalfEdge next) {
        this.next = next;
    }

    public HalfEdge Prev() {
        return prev;
    }

    public void setPrev(HalfEdge prev) {
        this.prev = prev;
    }

    public Face IncidentFace() {
        return incidentFace;
    }

    public void setIncidentFace(Face incidentFace) {
        this.incidentFace = incidentFace;
    }

    public HalfEdge() {}

    public HalfEdge(Vertex origin) {
        this.origin = origin;
    }

    public HalfEdge(Vertex origin, HalfEdge twin) {
        this(origin);
        this.twin = twin;
    }

    public HalfEdge(Vertex origin, HalfEdge twin, Face incidentFace) {
        this(origin, twin);
        this.incidentFace = incidentFace;
    }

    public HalfEdge(Vertex origin, HalfEdge twin, Face incidentFace, HalfEdge next, HalfEdge prev) {
        this(origin, twin, incidentFace);
        this.setNext(next);
        this.setPrev(prev);
    }
}
