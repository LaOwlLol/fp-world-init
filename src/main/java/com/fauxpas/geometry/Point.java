/**
 *
 * Point is a x,y pair representing a screen coordinate.
 Point objects are immutable.  If you need a new value, make a new Point!

 some methods borrowed from awjwerner https://github.com/ajwerner/fortune/blob/master/src/org/ajwerner/voronoi/Point.java
 include:
 *
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

package com.fauxpas.geometry;

public class Point implements Comparable<Point>{

    private double x;
    private double y;

    public Point(double _x, double _y) {
        this.x = _x;
        this.y = _y;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public long xPixel() {
        return Math.round(this.x());
    }

    public long yPixel() {
        return Math.round(this.x());
    }

    public double l1() {
        return this.x() + this.y();
    }

    public double l2() {
        return (this.x()*this.x()) + (this.y() *this.y());
    }

    public double euclideanLength() {
        return Math.sqrt(this.l2());
    }

    public Point normal() {
        return new Point ( this.x/this.euclideanLength() , this.y/this.euclideanLength() );
    }

    public Point scaled(double n) {
        return new Point(this.x * n, this.y *n);
    }

    public Point moved(Point _d) {
        return new Point(this.x+_d.x(), this.y+_d.y());
    }

    public double euclideanDistance(Point _other) {
        return Math.sqrt( Math.pow((this.x()-_other.x()), 2) + Math.pow((this.y()-_other.y()), 2));
    }

    public double distanceTo(Point _other) {
        return euclideanDistance(_other);
    }

    public static Point midpoint(Point p1, Point p2) {
        double x = (p1.x + p2.x) / 2;
        double y = (p1.y + p2.y) / 2;
        return new Point(x, y);
    }

    /**
     * Is a->b->c a counterclockwise turn?
     * @param a first point
     * @param b second point
     * @param c third point
     * @return { -1, 0, +1 } if a->b->c is a { clockwise, collinear; counterclocwise } turn.
     *
     * Copied directly from Point2D in Algs4 (Not taking credit for this guy)
     */
    public static int ccw(Point a, Point b, Point c) {
        double area2 = (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
        if      (area2 < 0) return -1;
        else if (area2 > 0) return +1;
        else                return  0;
    }

    public static int minYOrderedCompareTo(Point p1, Point p2) {
        if (p1.y < p2.y) return 1;
        if (p1.y > p2.y) return -1;
        if (p1.x == p2.x) return 0;
        return (p1.x < p2.x) ? -1 : 1;
    }

    @Override
    public int compareTo(Point o) {
        if ((this.x == o.x) || (Double.isNaN(this.x) && Double.isNaN(o.x))) {
            if (this.y == o.y) {
                return 0;
            }
            return (this.y < o.y) ? -1 : 1;
        }
        return (this.x < o.x) ? -1 : 1;
    }


    public String toString() {
        return String.format("(%.3f, %.3f)", this.x, this.y);
    }


}
