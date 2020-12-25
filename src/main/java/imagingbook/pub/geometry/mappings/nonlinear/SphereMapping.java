/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.nonlinear;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.Pnt2d.PntDouble;
import imagingbook.pub.geometry.mappings.Mapping2D;

/**
 * A non-linear mapping that produces a spherical distortion.
 * The transformation is implicitly inverted, i.e., maps target to source image
 * coordinates.
 * 
 * @author WB
 *
 */
public class SphereMapping implements Mapping2D {
	
	static double DefaultRefIdx = 1.8;
			
	private final double xc;			// center of sphere
	private final double yc;
	private final double rad;			// radius of sphere
	private final double refIdx;		// refraction index
   
	public SphereMapping(double xc, double yc, double rad) {
		this(xc, yc, rad, DefaultRefIdx);
	}
	
	public SphereMapping(double xc, double yc, double rad, double refIdx) {
		this.xc = xc;
		this.yc = yc;
		this.rad = rad;
		this.refIdx = refIdx;
	}

	@Override
	public Pnt2d applyTo(Pnt2d pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		double dx = x - xc;
		double dy = y - yc;
		double dx2 = dx * dx;
		double dy2 = dy * dy;
		double rad2 = rad * rad;

		double r2 = dx * dx + dy * dy;

		if (r2 > 0 && r2 < rad2) {
			double z2 = rad2 - r2;
			double z = Math.sqrt(z2);

			double xAlpha = Math.asin(dx / Math.sqrt(dx2 + z2));
			double xBeta = xAlpha - xAlpha * (1 / refIdx);
			double x1 = x - z * Math.tan(xBeta);

			double yAlpha = Math.asin(dy / Math.sqrt(dy2 + z2));
			double yBeta = yAlpha - yAlpha * (1 / refIdx);
			double y1 = y - z * Math.tan(yBeta);
			return PntDouble.from(x1, y1);
		}
		else { // otherwise leave point unchanged
			return PntDouble.from(x, y);
		}
	}
}




