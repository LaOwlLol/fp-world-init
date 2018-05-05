/**
 * The MIT License (MIT)

 Copyright (c) 2014 ajwerner

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in
 the Software without restriction, including without limitation the rights to
 use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 the Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fauxpas.fortunes.ajwerner;

import com.fauxpas.geometry.*;

/**
 * Created by ajwerner on 12/28/13.
 */
public class BreakPoint {
    private final Voronoi v;
    protected final Point s1, s2;
    private VoronoiEdge e;
    private HalfEdge in;
    private HalfEdge out;
    private boolean isEdgeLeft;
    public final Point edgeBegin;

    private double cacheSweepLoc;
    private Point cachePoint;

    public BreakPoint(Point left, Point right, VoronoiEdge e, HalfEdge _in, HalfEdge _out, boolean isEdgeLeft, Voronoi v) {
        this.v = v;
        this.s1 = left;
        this.s2 = right;
        this.e = e;
        this.in = _in;
        this.out = _out;
        this.isEdgeLeft = isEdgeLeft;
        this.edgeBegin = this.getPoint();
    }

    private static double sq(double d) {
        return d * d;
    }

    public void finish(Point vert, Graph g) {
        if (isEdgeLeft) {
            this.e.setP1(vert);
            Vertex v = g.getVertex(vert);
            g.addVertex(v);
            this.in.setOrigin(v);
            v.setIncidentHalfEdge(this.in);

            Face f = g.getFace(s1);
            f.addInnerComponents(this.in);
            g.addFace(f);
        }
        else {
            this.e.setP2(vert);
            Vertex v = g.getVertex(vert);
            g.addVertex(v);
            this.out.setOrigin(v);
            v.setIncidentHalfEdge(this.out);

            Face f = g.getFace(s2);
            f.addInnerComponents(this.out);
            g.addFace(f);
        }

    }

    public void finish(Graph g) {
        Point p = this.getPoint();
        if (isEdgeLeft) {
            this.e.setP1(p);
            Vertex v = g.getVertex(p);
            g.addVertex(v);
            this.in.setOrigin(v);
            v.setIncidentHalfEdge(this.in);

            Face f = g.getFace(s1);
            f.addInnerComponents(this.in);
            g.addFace(f);
        }
        else {
            this.e.setP2(p);
            Vertex v = g.getVertex(p);
            g.addVertex(v);
            this.out.setOrigin(v);
            v.setIncidentHalfEdge(this.out);

            Face f = g.getFace(s2);
            f.addInnerComponents(this.out);
            g.addFace(f);
        }
    }

    public Point getPoint() {
        double l = v.getSweepLoc();
        if (l == cacheSweepLoc) {
            return cachePoint;
        }
        cacheSweepLoc = l;

        double x,y;
        // Handle the vertical line case
        if (Double.compare(s1.y(), s2.y()) == 0) {
            x = (s1.x() + s2.x()) / 2; // x coordinate is between the two sites
            // comes from parabola focus-directrix definition:
            y = (sq(x - s1.x()) + sq(s1.y()) - sq(l)) / (2* (s1.y() - l));
        }
        else {
            // This method works by intersecting the line of the edge with the parabola of the higher point
            // I'm not sure why I chose the higher point, either should work
            double px = (s1.y() > s2.y()) ? s1.x() : s2.x();
            double py = (s1.y() > s2.y()) ? s1.y() : s2.y();
            double m = e.m;
            double b = e.b;

            double d = 2*(py - l);

            // Straight up quadratic formula
            double A = 1;
            double B = -2*px - d*m;
            double C = sq(px) + sq(py) - sq(l) - d*b;
            int sign = (s1.y() > s2.y()) ? -1 : 1;
            double det = sq(B) - 4 * A * C;
            // When rounding leads to a very very small negative determinant, fix it
            if (det <= 0) {
                x = -B / (2 * A);
            }
            else {
                x = (-B + sign * Math.sqrt(det)) / (2 * A);
            }
            y = m*x + b;
        }
        cachePoint = new Point(x, y);
        return cachePoint;
    }

    public String toString() {
        return String.format("%s \ts1: %s\ts2: %s", this.getPoint(), this.s1, this.s2);
    }

    public VoronoiEdge getEdge() {
        return this.e;
    }

    public HalfEdge getIn() {
        return this.in;
    }

    public HalfEdge getOut() {
        return this.out;
    }
}
