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
 * Created by ajwerner on 12/29/13.
 */
public abstract class ArcKey implements Comparable<ArcKey> {
    protected abstract Point getLeft();
    protected abstract Point getRight();

    public int compareTo(ArcKey that) {
        Point myLeft = this.getLeft();
        Point myRight = this.getRight();
        Point yourLeft = that.getLeft();
        Point yourRight = that.getRight();

        // If one arc contains the query then we'll say that they're the same
        if (((that.getClass() == ArcQuery.class) || (this.getClass() == ArcQuery.class)) &&
                ((myLeft.x() <= yourLeft.x() && myRight.x() >= yourRight.x()) ||
                        (yourLeft.x() <= myLeft.x() && yourRight.x() >= myRight.x() ))) {
            return 0;
        }

        if (Double.compare(myLeft.x(), yourLeft.x()) == 0 && Double.compare( myRight.x(), yourRight.x()) == 0) return 0;
        if (myLeft.x() >= yourRight.x()) return 1;
        if (myRight.x() <= yourLeft.x()) return -1;

        return Point.midpoint(myLeft, myRight).compareTo(Point.midpoint(yourLeft, yourRight));
    }
}
