package imagingbook.pub.geometry.moments;

import java.util.HashSet;
import java.util.Set;

import imagingbook.lib.math.Arithmetic;
import imagingbook.pub.geometry.basic.Pnt2d;

/**
 * This class defines static methods for moment calculations on 2D point sets.
 * TODO: make more efficient versions!
 * 
 * @author WB
 *
 */
public abstract class Moments2D {

	public static double ordinaryMoment(Iterable<Pnt2d> points, int p, int q) {
		double Mpq = 0.0;
		if (p == 0 && q == 0) {	// just count the number of points
			for (@SuppressWarnings("unused") Pnt2d pnt : points) {
				Mpq += 1;
			}
		}
		else {
			for (Pnt2d pnt : points) {
				Mpq += Math.pow(pnt.getX(), p) * Math.pow(pnt.getY(), q);
			}
		}
		return Mpq;
	}

	public static double centralMoment(Iterable<Pnt2d> points, int p, int q) {
		double a = ordinaryMoment(points, 0, 0); // region area
		if (Arithmetic.isZero(a)) {
			throw new RuntimeException("empty point set");
		}
		double xCtr = ordinaryMoment(points, 1, 0) / a;
		double yCtr = ordinaryMoment(points, 0, 1) / a;
		double cMpq = 0.0;
		for (Pnt2d pnt : points) {
			cMpq += Math.pow(pnt.getX() - xCtr, p) * Math.pow(pnt.getY() - yCtr, q);
		}
		return cMpq;
	}
	
	public static double normalizedCentralMoment(Iterable<Pnt2d> points, int p, int q) {
		double a = ordinaryMoment(points, 0, 0);
		double scale = Math.pow(a, 0.5 * (p + q + 2));
		return centralMoment(points, p, q) / scale;
	}
	
	
	public static void main(String[] args) {
		Set<Pnt2d> points = new HashSet<>();
		points.add(Pnt2d.from(10, 15));
		points.add(Pnt2d.from(3, 7));
		points.add(Pnt2d.from(-1, 5));
		points.add(Pnt2d.from(-1, 5));
		System.out.println("set size = " + points.size());
		
		System.out.println("m00 = " + ordinaryMoment(points, 0, 0));
		System.out.println("m10 = " + ordinaryMoment(points, 1, 0));
		System.out.println("m01 = " + ordinaryMoment(points, 0, 1));
		
		double a = ordinaryMoment(points, 0, 0);
		System.out.println("a = " + a);
		System.out.println("xc = " + (ordinaryMoment(points, 1, 0) / a));
		System.out.println("yc = " + (ordinaryMoment(points, 0, 1) / a));	
		
		System.out.println("mu10 = " + centralMoment(points, 1, 0));
		System.out.println("mu01 = " + centralMoment(points, 0, 1));
		System.out.println("mu11 = " + centralMoment(points, 1, 1));
		System.out.println("mu20 = " + centralMoment(points, 2, 0));
		System.out.println("mu02 = " + centralMoment(points, 0, 2));
	}
	
}

/*
set size = 3
m00 = 3.0
m10 = 12.0
m01 = 27.0
a = 3.0
xc = 4.0
yc = 9.0
mu10 = 0.0
mu01 = 0.0
mu11 = 58.0
mu20 = 62.0
mu02 = 56.0
*/
