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

import com.fauxpas.geometry.Point;

/**
 * Created by ajwerner on 12/28/13.
 */
public class VoronoiEdge {
    public final Point site1, site2;
    public final double m, b; // parameters for line that the edge lies on
    public final boolean isVertical;
    private Point p1, p2;

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public VoronoiEdge(Point site1, Point site2) {
        this.site1 = site1;
        this.site2 = site2;
        isVertical = (site1.y() == site2.y()) ? true : false;
        if (isVertical) m = b = 0;
        else {
            m = -1.0 / ((site1.y() - site2.y()) / (site1.x() - site2.x()));
            Point midpoint = Point.midpoint(site1, site2);
            b = midpoint.y() - m*midpoint.x();
        }
    }

    public Point intersection(VoronoiEdge that) {
        if (this.m == that.m && this.b != that.b && this.isVertical == that.isVertical) return null; // no intersection
        double x, y;
        if (this.isVertical) {
            x = (this.site1.x()+ this.site2.x()) / 2;
            y = that.m*x + that.b;
        }
        else if (that.isVertical) {
            x = (that.site1.x()+ that.site2.x()) / 2;
            y = this.m*x + this.b;
        }
        else {
            x = (that.b - this.b) / (this.m - that.m);
            y = m * x + b;
        }
        return new Point(x, y);
    }
}
