package com.fauxpas.fortunes;

import com.fauxpas.geometry.Point;

import java.util.Comparator;

public class FortuneHelpers {
	public static boolean areCounterClockWise(Point _a, Point _b, Point _c) {
		return (_b.x() - _a.x())*(_c.y() -_a.y()) - (_c.x() - _a.x())*(_b.y() - _a.y()) > 0;
	}

	public static boolean inCircle(Point _a, Point _b, Point _c, Point _test) {
		double ax_ = _a.x()-_test.x();
		double ay_ = _a.y()-_test.y();
		double bx_ = _b.x()-_test.x();
		double by_ = _b.y()-_test.y();
		double cx_ = _c.x()-_test.x();
		double cy_ = _c.y()-_test.y();

		return (
			((ax_*ax_) + (ay_*ay_)) * ((bx_*cy_) - (cx_*by_)) - 
			((bx_*bx_) + (by_*by_)) * ((ax_*cy_) - (cx_*ay_)) +
			((cx_*cx_) + (cy_*cy_)) * ((ax_*by_) - (bx_*ay_))
			) > 0;
	}

	public static boolean isADirectlyBelowB(Point _a, Point _b) {
		return (_a.compareX(_b) == 0 && _a.compareY(_b) == -1);
	}

	public static boolean isQAboveParabolaPL(Point _q, Point _p, double _l) {
		return _q.euclideanDistance(_p) < (_q.y() - _l);
	}

	public static boolean isQBelowParabolaPL(Point _q, Point _p, double _l) {
		return _q.euclideanDistance(_p) > (_q.y() - _l);
	}

	public static boolean isQOnParabolaPL(Point _q, Point _p, double _l) {
		return ((_q.euclideanDistance(_p) - (_q.y() - _l) ) < 0.001);
	}

	public static int compareQtoParabolaPL(Point _q, Point _p, double _l) {
		if (!isQOnParabolaPL(_q, _p, _l)) {
			if ( isQAboveParabolaPL(_q, _p, _l) ) {
				return -1;
			}
			else if ( isQBelowParabolaPL(_q, _p, _l) ) {
				return 1;
			}
			else {
				//how did this happen?
				return 0;
			}
		}
		else  {
			return 0;
		}
	}

	public static Point VoronoiTransfrom(Point _z, Point _s) {
		return new Point(  _z.x() , (_z.y() + _z.euclideanDistance(_s)) );
	}

	public static Point getMidPoint(Point _a, Point _b) {
		return new Point( _a.x() + ((_a.x() - _b.x())/2),  _a.y()+ ((_a.y() - _b.y())/2)  );
	}

	public static Point perpendicular(Point _a, Point _b) {
		double dx = _b.x() - _a.x();
		double dy = _b.y() - _a.y();

		return _b.moved(new Point(-dy, dx).scaled(_a.euclideanDistance(_b)/2.0));
	}


	/**
	 * Get the location of the breakpoint which is at
	 * a point q the center of a circle passing through
	 * left site, right site, and event (_l) site.
	 *
	 * note: to avoid degenerate cases (null pointer exception) look for a child which
	 * has location.
	 *
	 * @param _a left most point.
	 * @param _b top most point (really lowest y on a canvas).
	 * @param _c right most Point.
	 *
	 * @return center of the circle passing through left, right, and event sites.
	 */
	public static Point getCenterOfCircumCircle(Point _a, Point _b, Point _c) {
		//calculate slope of points on circle. ma = (y2-y1)/(x2-x1) mb = (y3-y2)/(x3-x2)
		double ma = (_b.y() - _a.y()) / ( _b.x() - _a.x() );
		double mb = (_c.y() - _b.y() ) / (_c.x() - _b.x() );

		//calculate numerator of the x coordinates from perpendiculars to ma and mb
		double numerator = ma*mb*(_a.y() - _c.y()) +
				mb * (_a.x() + _b.x()) -
				ma*( _b.x() + _c.x() );

		double x = numerator / (2* (mb - ma));
		double y =  (-1 / ma) * (x - (_a.x() + _b.x()) * 0.5) + (_a.y() + _b.y()) * 0.5;
		return new Point (x , y);
	}
}