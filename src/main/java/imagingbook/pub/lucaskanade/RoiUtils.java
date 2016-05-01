/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package imagingbook.pub.lucaskanade;

import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.mappings.linear.LinearMapping;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

@Deprecated
public class RoiUtils {
	
	@Deprecated
	public static List<Point2D> getWarpedPointsCentered(Roi roi, LinearMapping W) {
		Rectangle bounds = roi.getBounds();
		List<Point2D> oPts = new LinkedList<Point2D>();
		float xC = bounds.width/2;
		float yC = bounds.height/2;
		oPts.add(new Point2D.Float(0 - xC, 0 - yC));
		oPts.add(new Point2D.Float(bounds.width - xC, 0 - yC));
		oPts.add(new Point2D.Float(bounds.width - xC, bounds.height - yC));
		oPts.add(new Point2D.Float(0 - xC, bounds.height - yC));
		List<Point2D> wPts = new LinkedList<Point2D>();
		for (Point2D op : oPts) {
			wPts.add(W.applyTo(op));
			//pts.add(ipm);
		}
		return wPts;
	}
	
	@Deprecated
	public static Roi makePolygon(Point2D[] points, double strokeWidth, Color color) {
		Path2D poly = new Path2D.Double();
		if (points.length > 0) {
			poly.moveTo(points[0].getX(), points[0].getY());
			for (int i = 1; i < points.length; i++) {
				poly.lineTo(points[i].getX(), points[i].getY());
			}
			poly.closePath();
		}
		Roi shapeRoi = new ShapeRoi(poly);
		shapeRoi.setStrokeWidth(strokeWidth);
		shapeRoi.setStrokeColor(color);
		return shapeRoi;
	}
	
	
//	@Deprecated
//	public static void listPolygon(List<Point2D> points, String title) {
//		IJ.log("Polygon " + title + ":");
//		int i = 0;
//		for (Point2D p : points) {
//			i++;
//			IJ.log(" v" + i + "= " + p.toString());
//		}
//	}
	
	// ---------------------------------------------------------------------------
	
	@Deprecated
	public static FloatProcessor getUnwarpedImage(ImageProcessor I, LinearMapping W, int w, int h) {
		FloatProcessor J = new FloatProcessor(w, h);
		int uc = w/2;				// center (origin) of R
		int vc = h/2;
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				double[] x = {u - uc, v - vc};	// position w.r.t. the center of R
				double[] xw = W.applyTo(x);		// warp from x -> xw
				float val = (float) I.getInterpolatedValue(xw[0], xw[1]);
				J.setf(u, v, val);
			}
		}
		return J;
	}

}
