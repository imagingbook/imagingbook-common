package imagingbook.pub.geometry.basic;

import static imagingbook.lib.math.Arithmetic.sqr;

import java.util.Collection;

/** 
 * Interface specifying the behavior of a simple 2D point.
 */
public interface Point {
	double getX();
	double getY();
	
	static Point create(double x, double y) {
		return new Imp(x, y);
	}
	
	static Point create(double[] xy) {
		return new Imp(xy[0], xy[1]);
	}
	
	default double[] toArray() {
		return new double[] {this.getX(), this.getY()};
	}
	
	static Point[] toArray(Collection<Point> points) {
		return points.toArray(new Point[0]);
	}
	
	static double distance(Point p, Point q) {
		return Math.sqrt(distance2(p, q));
	}
	
	static double distance2(Point p, Point q) {
		return sqr(q.getX() - p.getX()) + sqr(q.getY() - p.getY());
	}
	
	default double distance2(Point other) {
		return distance2(this, other);
	}
	
	default double distance(Point other) {
		return distance(this, other);
	}
	
	
	/**
	 * Simple fallback implementation of the {@link Point} interface.
	 * If dependency on AWT is to be avoided, just implement your own.
	 */
	public class Imp extends java.awt.geom.Point2D.Double implements Point {
		private static final long serialVersionUID = 1L;

		public Imp(double x, double y) {
			super(x, y);
		}
		
		@Override
		public String toString() {
			return String.format("Point[%.3f, %.3f]", this.getX(), this.getY());
		}
	}

}
