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
 * {@link Point.Double}.
 * 
 * TODO: finish 'Int'-implementation
 * TODO: avoid 'Double', may be confused
 */
public interface Point {
	
	public static final Point ZERO = Point.create(0, 0);
	
	double getX();
	double getY();
//	Point duplicate();
	
	public static Point create(double x, double y) {
		return new Double(x, y);
	}
	
	public static Point create(double[] xy) {
		return new Double(xy[0], xy[1]);
	}
	
	public static Point create(int[] xy) {
		return new Double(xy[0], xy[1]);
	}
	
	public static Point create(Point p) {
		return p.duplicate();
	}
	
	public static Point create(Point2D p) {
		return new Double(p.getX(), p.getY());
	}
	
	public static Point create(Vector2D vec) {
		return new Double(vec.getX(), vec.getY());
	}
	
	public static Point.Int create(int x, int y) {
		return new Int(x, y);
	}
	
	public static Point.Int create(java.awt.Point p) {
		return new Int(p.x, p.y);
	}
	
	// ----------------------------------------------------------
	
	public default Point duplicate() {
		throw new UnsupportedOperationException();
	}
	
	public default double[] toArray() {
		return new double[] {this.getX(), this.getY()};
	}
	
	public default Point2D toPoint2D() {
		return new Point2D.Double(this.getX(), this.getY());
	}
	
	public default Vector2D toVector2D() {
		return new Vector2D(getX(), getY());
	}
	
//	public static Point[] toArray(Collection<Point> points) {
//		return points.toArray(new Point[0]);
//	}
	
	public default boolean equals(Point p) {
		return Arithmetic.isZero(this.getX() - p.getX()) && Arithmetic.isZero(this.getY() - p.getY());
	}
	
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

	// ----------------------------------------------------------
	
	/**
	 * Simple fallback implementation of the {@link Point} interface.
	 * Use {@link Point#create(double, double)} or similar to instantiate.
	 * TODO: naming is prelimiary!!
	 */
	public class Double implements Point {

		private final double x, y;

		private Double(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US, "%s[%.3f, %.3f]", 
					getClass().getSimpleName(), x, y);
		}

		@Override
		public Point.Double duplicate() {
			return new Double(x, y);
		}
		
		@Override	// unfinished!
		public boolean equals(Object other) {
			if (other instanceof Point) {
				Point p = (Point) other;
				return Arithmetic.isZero(this.x - p.getX()) && Arithmetic.isZero(this.y - p.getY());
			}
			else {
				return false;
			}
		}
	}
	
	// ----------------------------------------------------------
	
	public class Int implements Point {
		
		private final int x, y;
		
		public Int(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US, "%s[%d, %d]", 
					getClass().getSimpleName(), x, y);
		}

		@Override
		public Point.Int duplicate() {
			return new Int(x, y);
		}
		
	}
	
	

}
