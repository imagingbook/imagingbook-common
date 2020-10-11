/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.geometry.hulls;

import ij.IJ;
import imagingbook.lib.math.Arithmetic;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.basic.Point;

/**
 * 
 * @author WB
 * @version 2020/10/11
 * 
 */
public class AxisAlignedBoundingBox {
	
	private final double[][] boundingBox;
	
	public AxisAlignedBoundingBox(Iterable<Point> points) {
		this.boundingBox = makeBox(points);
	}
	
	public Point[] getCornerPoints() {
		Point[] cpts = new Point[boundingBox.length];
		for (int i = 0; i < boundingBox.length; i++) {
			cpts[i] = Point.create(boundingBox[i][0], boundingBox[i][1]);
		}
		return cpts;
	}
		
	/**
	 * Calculates the major axis-aligned bounding box of 
	 * the supplied region, as a sequence of four point
	 * coordinates (A, B, C, D).
	 * @param points binary region
	 * @return the region's bounding box as a sequence of 4 coordinates,
	 * (A, B, C, D)
	 */
	private double[][] makeBox(Iterable<Point> points) {
		double theta = getRegionOrientation(points);	// TODO: can be done without trigonom. functions!
		
		double xa = Math.cos(theta);
		double ya = Math.sin(theta);
		double[] ea = {xa,  ya};
		double[] eb = {ya, -xa};
		
		double amin = Double.POSITIVE_INFINITY;
		double amax = Double.NEGATIVE_INFINITY;
		double bmin = Double.POSITIVE_INFINITY;
		double bmax = Double.NEGATIVE_INFINITY;
		
		for (Point p : points) {
			double a = p.getX() * xa + p.getY() * ya;	// project (x,y) on the major axis vector
			double b = p.getX() * ya - p.getY() * xa;	// project (x,y) on perpendicular vector
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
	 * @param points binary region
	 * @return orientation of the major axis (angle in radians)
	 */
	private double getRegionOrientation(Iterable<Point> points) {
		double[] centroid = getCentroid(points);
		final double xc = centroid[0];
		final double yc = centroid[1];
		double mu11 = 0;
		double mu20 = 0;
		double mu02 = 0;

		for (Point p : points) {
			double dx = (p.getX() - xc);
			double dy = (p.getY() - yc);
			mu11 = mu11 + dx * dy;
			mu20 = mu20 + dx * dx;
			mu02 = mu02 + dy * dy;
		}
		
		if (Arithmetic.isZero(mu11) && Arithmetic.isZero(mu20 - mu02)) {
			IJ.log("point set orientation is undefined!");
			//return Double.NaN;
		}
		
		return 0.5 * Math.atan2(2 * mu11, mu20 - mu02);
	}
	
	private double[] getCentroid(Iterable<Point> points) {
		int n = 0;
		double sumX = 0;
		double sumY = 0;
		for (Point p : points) {
			sumX += p.getX();
			sumY += p.getY();
			n++;
		}
		if (n == 0) {
			throw new IllegalArgumentException("empty point sequence!");
		}
		return new double[] {sumX/n, sumY/n};
	}
	
	// --------------------------------------------------------------------
	
	
	public static void main(String[] args) {
		
		for (int i = -180; i <= 180; i++) {
			double angle = i * 2 * Math.PI / 360;
			double A = Math.sin(angle);
			double B = Math.cos(angle);
			double T = 0.5 * Math.atan2(A, B);
			double sT = Math.sin(T);
			double cT = Math.cos(T);
			double T2 = Math.atan2(A, 1 + B);
			double T3 = Math.atan2(Math.sqrt(0.5 * (1 - B)), Math.sqrt(0.5 * (1 + B)));
			System.out.format("%4d a=%6.3f | A=%5.2f B=%5.2f | T=%6.3f sT=%5.2f cT=%5.2f | T2=%6.3f | T3=%6.3f\n", i, angle, A, B, T, sT, cT, T2, T3);
		}
	}

}
