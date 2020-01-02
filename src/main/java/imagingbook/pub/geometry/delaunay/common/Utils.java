package imagingbook.pub.geometry.delaunay.common;

import java.util.Collection;

public abstract class Utils {
	
	public static Point[] makeOuterTriangle(Collection<Point> points) {
		double xmin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymin = xmin;
		double ymax = xmax;
		
		for (Point p : points) {
			double x = p.getX();
			double y = p.getY();
			xmin = Math.min(x, xmin);
			xmax = Math.max(x, xmax);
			ymin = Math.min(y, ymin);
			ymax = Math.max(y, ymax);
		}
		return makeOuterTriangle(xmin, xmax, ymin, ymax);
	}
	
	public static Point[] makeOuterTriangle(double xmin, double xmax, double ymin, double ymax) {
		double width = xmax - xmin;
		double height = ymax - ymin;
		double diam = Math.max(width,  height);
		double xc = xmin + width / 2;
		double yc = ymin + height / 2;
		double s = 50;
		return new Point[] {
				Point.create(xc, yc + s * diam),
				Point.create(xc + s * diam, yc),
				Point.create(xc - s * diam, yc - s * diam)
		};
	}
	
	public static Point[] makeOuterTriangle(double width, double height) {
		return makeOuterTriangle(0, width, 0, height);
	}

}
