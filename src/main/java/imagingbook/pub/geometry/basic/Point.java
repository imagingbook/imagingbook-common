package imagingbook.pub.geometry.basic;

import java.util.Collection;

import imagingbook.pub.geometry.basic.Point;

/** 
 * Interface specifying the behavior of a simple 2D point.
 */
public interface Point {
	double getX();
	double getY();
	
	public static Point create(double x, double y) {
		return new Imp(x, y);
	}
	
	public static Point create(double[] xy) {
		return new Imp(xy[0], xy[1]);
	}
	
	default double[] toArray() {
		return new double[] {this.getX(), this.getY()};
	}
	
	public static Point[] toArray(Collection<Point> points) {
		return points.toArray(new Point[0]);
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
