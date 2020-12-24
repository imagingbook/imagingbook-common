package imagingbook.pub.geometry.basic;

import static imagingbook.lib.math.Arithmetic.sqr;

import java.awt.geom.Point2D;
import java.util.Locale;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import imagingbook.lib.math.Arithmetic;

/** 
 * Interface specifying the behavior of a simple 2D point with 
 * floating-point coordinates.
 * It is used to accommodate different (legacy) point implementations 
 * in a common API.
 * Use {@link Point#create(double, double)} or similar to 
 * create new point instances using the default implementation class 
 * {@link Point.Imp}.
 */
public interface Point {
	
	public static final Point ZERO = Point.create(0, 0);
	
	double getX();
	double getY();
	
	public static Point create(double x, double y) {
		return new Imp(x, y);
	}
	
	public static Point create(double[] xy) {
		return new Imp(xy[0], xy[1]);
	}
	
	public static Point create(int[] xy) {
		return new Imp(xy[0], xy[1]);
	}
	
	public static Point create(Point p) {
		return new Imp(p.getX(), p.getY());
	}
	
	public static Point create(Point2D p) {
		return new Imp(p.getX(), p.getY());
	}
	
	public static Point create(Vector2D vec) {
		return new Imp(vec.getX(), vec.getY());
	}
	
	// ----------------------------------------------------------
	
	public default double[] toArray() {
		return new double[] {this.getX(), this.getY()};
	}
	
	public default Point2D toPoint2D() {
		return new Point2D.Double(getX(), getY());
	}
	
	public default Vector2D toVector2D() {
		return new Vector2D(getX(), getY());
	}
	
//	public static Point[] toArray(Collection<Point> points) {
//		return points.toArray(new Point[0]);
//	}
	
	// ----------------------------------------------------------
	
	public static double distance(Point p, Point q) {
		return Math.sqrt(distance2(p, q));
	}
	
	public static double distance2(Point p, Point q) {
		return sqr(q.getX() - p.getX()) + sqr(q.getY() - p.getY());
	}
	
	public default double distance2(Point other) {
		return distance2(this, other);
	}
	
	public default double distance(Point other) {
		return distance(this, other);
	}
	
	public default boolean equals(Point a, Point b) {
		return Arithmetic.isZero(distance2(a, b), 1E-6);
	}
	
	/**
	 * Simple fallback implementation of the {@link Point} interface.
	 * Use {@link Point#create(double, double)} or similar to instantiate.
	 */
	public class Imp implements Point {
		private final double x, y;

		private Imp(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US, "%s[%.3f, %.3f]", 
					getClass().getSimpleName(), getX(), getY());
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}
	}

}
