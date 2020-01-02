package imagingbook.pub.geometry.delaunay.common;

import imagingbook.pub.geometry.delaunay.common.Point;

public interface Point {
	double getX();
	double getY();
	
	public static Point create(double x, double y) {
		return new Imp(x, y);
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
	}

}
