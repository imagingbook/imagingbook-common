package imagingbook.pub.geometry.delaunay.common;

import imagingbook.pub.geometry.delaunay.common.Point;

public interface Point {
	double getX();
	double getY();
	
	/**
	 * Simple fallback implementation of the {@link Point} interface.
	 * If no dependency on AWT is desired, just implement your own.
	 */
	public class Pnt extends java.awt.geom.Point2D.Double implements Point {
		private static final long serialVersionUID = 1L;

		public Pnt(double x, double y) {
			super(x, y);
		}
	}

}
