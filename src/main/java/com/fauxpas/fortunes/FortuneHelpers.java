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

	public static boolean isQAboveParabolaPL(Point _q, Point _p, double _l) {
		return _q.euclideanDistance(_p) < (_q.y() - _l);
	}

	public static boolean isQBelowParabolaPL(Point _q, Point _p, double _l) {
		return _q.euclideanDistance(_p) > (_q.y() - _l);
	}

	public static boolean isQOnParabolaPL(Point _q, Point _p, double _l) {
		return ((_q.euclideanDistance(_p) - (_q.y() - _l) ) < 0.001);
	}

	public static int compareYNatural(FortuneEvent o1, FortuneEvent o2) {
		return o1.getSite().compareY(o2.getSite());
	}

	public static int compareYReverse(FortuneEvent o1, FortuneEvent o2) {
		return o2.getSite().compareY(o1.getSite());
	}

	public static int compareX(FortuneEvent o1, FortuneEvent o2) {
		return o1.getSite().compareX(o2.getSite());
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

	public static boolean isADirectlyBelowB(Point _a, Point _b) {
		return (_a.compareX(_b) == 0 && _a.compareY(_b) == -1);
	}

	public static Point getUnitVectorBetween(Point _a, Point _b) {
		return new Point( _a.x() + ((_a.x() - _b.x())/2),  _a.y()+ ((_a.y() - _b.y())/2)  );
		
	}

}