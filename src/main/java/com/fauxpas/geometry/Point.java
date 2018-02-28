package com.fauxpas.geometry;

/*Point is a x,y pair representing a screen coordinate.
Point objects are immutable.  If you need a new value, make a new Point!
 */

public class Point {

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

    public double euclideanDistance(Point _other) {
        return Math.sqrt( Math.pow((this.x()-_other.x()), 2)+Math.pow((this.y()-_other.y()), 2));
    }

    public boolean effectivelyEqual(Point _other, double _tolerance) {
        return ( Math.abs(this.x()- _other.x()) < _tolerance)
                && ( Math.abs(this.y() - _other.y()) < _tolerance );
    }

    public static Point EuclideanShift(Point _z, Point _s) {
        return new Point(  _z.x() , (_z.y() + _z.euclideanDistance(_s)) );
    }
}
