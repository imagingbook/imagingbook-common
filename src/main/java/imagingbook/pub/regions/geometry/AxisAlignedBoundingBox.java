/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.regions.geometry;

import java.awt.Point;
import java.awt.geom.Point2D;

import ij.process.ImageProcessor;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;



public class AxisAlignedBoundingBox {
	
	private final double[][] boundingBox;
	
	public AxisAlignedBoundingBox(BinaryRegion r) {
		boundingBox = makeBox(r);
	}
	
	
//	public double[][] getCorners() {
//		return boundingBox;
//	}
	
	public Point2D[] getCornerPoints() {
		Point2D[] cpts = new Point2D[boundingBox.length];
		for (int i = 0; i < boundingBox.length; i++) {
			cpts[i] = new Point2D.Double(boundingBox[i][0], boundingBox[i][1]);
		}
		return cpts;
	}
		
	/**
	 * Calculates the major axis-aligned bounding box of 
	 * the supplied region, as a sequence of four point
	 * coordinates (A, B, C, D).
	 * @param r binary region
	 * @return the region's bounding box as a sequence of 4 coordinates,
	 * (A, B, C, D)
	 */
	private double[][] makeBox(BinaryRegion r) {
		double theta = getRegionOrientation(r);
		double xa = Math.cos(theta);
		double ya = Math.sin(theta);
		double[] ea = {xa,  ya};
		double[] eb = {ya, -xa};
		
		double amin = Double.POSITIVE_INFINITY;
		double amax = Double.NEGATIVE_INFINITY;
		double bmin = Double.POSITIVE_INFINITY;
		double bmax = Double.NEGATIVE_INFINITY;
		
		for (Point p : r) {
			double a = p.x * xa + p.y * ya;	// project (x,y) on the major axis vector
			double b = p.x * ya - p.y * xa;	// project (x,y) on perpendicular vector
			amin = Math.min(a, amin);
			amax = Math.max(a, amax);
			bmin = Math.min(b, bmin);
			bmax = Math.max(b, bmax);
		}
					
		double[] A = Matrix.add(Matrix.multiply(amin, ea), Matrix.multiply(bmin, eb));
		double[] B = Matrix.add(Matrix.multiply(amin, ea), Matrix.multiply(bmax, eb));
		double[] C = Matrix.add(Matrix.multiply(amax, ea), Matrix.multiply(bmax, eb));
		double[] D = Matrix.add(Matrix.multiply(amax, ea), Matrix.multiply(bmin, eb));
		
		return new double[][] {A, B, C, D};
	}
	
	/**
	 * Calculates the orientation of major axis.
	 * TODO: move this somewhere else (into class BinaryRegion)
	 * @param r binary region
	 * @return orientation of the major axis (angle in radians)
	 */
	private double getRegionOrientation(BinaryRegion r) {
		final double xc = r.getXc();
		final double yc = r.getYc();
		double mu11 = 0;
		double mu20 = 0;
		double mu02 = 0;
		for (Point p : r) {
			double dx = (p.x - xc);
			double dy = (p.y - yc);
			mu11 = mu11 + dx * dy;
			mu20 = mu20 + dx * dx;
			mu02 = mu02 + dy * dy;
		}
		
		return 0.5 * Math.atan2(2 * mu11, mu20 - mu02);
	}
	
	// drawing --------------------------------------------
	
	public void draw(ImageProcessor ip) {
		drawLine(ip, boundingBox[0], boundingBox[1]);
		drawLine(ip, boundingBox[1], boundingBox[2]);
		drawLine(ip, boundingBox[2], boundingBox[3]);
		drawLine(ip, boundingBox[3], boundingBox[0]);
	}
	
	private void drawLine(ImageProcessor ip, double[] p1, double[] p2) {
		int u1 = (int) Math.round(p1[0]);
		int v1 = (int) Math.round(p1[1]);
		int u2 = (int) Math.round(p2[0]);
		int v2 = (int) Math.round(p2[1]);
		ip.drawLine(u1, v1, u2, v2);	
	}

}
