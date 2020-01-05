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
		return Math.sqrt(sqr(q.getX() - p.getX()) + sqr(q.getY() - p.getY()));
	}
	
	
	/**
	 * Simple fallback implementation of the {@link Point} interface.
	 * If no dependency on AWT is desired, just implement your own.
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
