package imagingbook.pub.hough.lines;

import ij.IJ;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import imagingbook.lib.ij.CustomOverlay;

import static imagingbook.lib.math.Arithmetic.sqr;

import java.awt.Color;
import java.awt.geom.Path2D;

public class HoughLineOverlay extends CustomOverlay<HoughLine> {

	/**
	 * Creates a vector line to be used an element in an ImageJ graphic overlay
	 * (see {@link ij.gui.Overlay}). The length of the displayed line 
	 * is measured from its center point (the point closest to the reference
	 * point) in both directions.
	 * 
	 * @param line the algebraic (Hough) line
	 * @return the overlay element
	 */
	@Override
	public Roi makeRoi(HoughLine line) {
		double xRef = line.getXref();
		double yRef = line.getYref();
		double length = Math.sqrt(sqr(xRef) + sqr(yRef));
		double angle = line.getAngle();
		double radius = line.getRadius();
		// unit vector perpendicular to the line
		double dx = Math.cos(angle);	
		double dy = Math.sin(angle);
		// calculate the line's center point (closest to the reference point)
		double x0 = xRef + radius * dx;
		double y0 = yRef + radius * dy;
		// calculate the line end points (using normal vectors)
//		float x1 = (float) (x0 + dy * length);
//		float y1 = (float) (y0 - dx * length);
//		float x2 = (float) (x0 - dy * length);
//		float y2 = (float) (y0 + dx * length);
//		float[] xpoints = { x1, x2 };
//		float[] ypoints = { y1, y2 };
		//Roi roi = new PolygonRoi(xpoints, ypoints, Roi.POLYLINE);
		
		double x1 = x0 + dy * length;
		double y1 = y0 - dx * length;
		double x2 = x0 - dy * length;
		double y2 = y0 + dx * length;
		Path2D path = new Path2D.Double();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		Roi roi = new ShapeRoi(path);
		return roi;
	}
	
	public void markReferencePoint(double xc, double yc, Color color) {
		double markerSize = 2.0;
		IJ.log("xc = " + xc);
		IJ.log("yc = " + yc);
		Path2D path = new Path2D.Double();
		path.moveTo(xc - markerSize, yc);
		path.lineTo(xc + markerSize, yc);
		path.moveTo(xc, yc - markerSize);
		path.lineTo(xc, yc + markerSize);
		ShapeRoi cross = new ShapeRoi(path);
		cross.setStrokeWidth(0.3);
		cross.setStrokeColor(color);
		this.addRoi(cross, true);
	}

}
