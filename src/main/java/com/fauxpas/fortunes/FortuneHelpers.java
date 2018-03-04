package com.fauxpas.fortunes;

import com.fauxpas.geometry.Point;

import java.util.Comparator;

public class FortuneHelpers {

	public static final double MAX_WIDTH = 600.0;

	public static final double MAX_HEIGHT = 500.0;

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

	public static class EventOrder implements Comparator<FortuneEvent> {

		@Override
		public int compare(FortuneEvent _e1, FortuneEvent _e2) {
			return _e1.getSite().compareY(_e2.getSite());
		}
	}
}