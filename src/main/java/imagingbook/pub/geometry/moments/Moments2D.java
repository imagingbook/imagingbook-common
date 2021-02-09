package imagingbook.pub.geometry.moments;

import java.util.Collection;

import imagingbook.pub.geometry.basic.Pnt2d;

/**
 * This class defines static methods for moment calculations on 2D point sets.
 * TODO: make more efficient versions!
 * 
 * @author WB
 *
 */
public abstract class Moments2D {

	public static double ordinaryMoment(Collection<? extends Pnt2d> points, int p, int q) {
		double Mpq = 0.0;
		for (Pnt2d pnt : points) {
			Mpq += Math.pow(pnt.getX(), p) * Math.pow(pnt.getY(), q);
		}
		return Mpq;
	}

	public static double centralMoment(Collection<? extends Pnt2d> points, int p, int q) {
		double m00 = ordinaryMoment(points, 0, 0); // region area
		double xCtr = ordinaryMoment(points, 1, 0) / m00;
		double yCtr = ordinaryMoment(points, 0, 1) / m00;
		double cMpq = 0.0;
		for (Pnt2d pnt : points) {
			cMpq += Math.pow(pnt.getX() - xCtr, p) * Math.pow(pnt.getY() - yCtr, q);
		}
		return cMpq;
	}
	
	public static double normalizedCentralMoment(Collection<? extends Pnt2d> points, int p, int q) {
		double m00 = ordinaryMoment(points, 0, 0);
		double scale = Math.pow(m00, 0.5 * (p + q + 2));
		return centralMoment(points, p, q) / scale;
	}

}
