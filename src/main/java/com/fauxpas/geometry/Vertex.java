package com.fauxpas.geometry;

import com.fauxpas.geometry.Point;

public class Vertex {

    /**
     * The location of this Vertex.
     */
    private Point coordinates;

    /**
     * An edge (arbitrary) who's origin is this vertex.
     */
    private HalfEdge incidentHalfEdge;

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Computational Geometry (Berg el al) p. 31
     * @return An edge with this vertex as it's origin.
     */
    public HalfEdge IncidentEdge() {
        return incidentHalfEdge;
    }

    public void setIncidentHalfEdge(HalfEdge incidentHalfEdge) {
        this.incidentHalfEdge = incidentHalfEdge;
    }

    public Vertex( Point coordinates, HalfEdge incidentHalfEdge) {
        this(coordinates);
        this.setIncidentHalfEdge(incidentHalfEdge);
    }

    public Vertex(Point coordinates) {
        this.coordinates = coordinates;
    }
}
