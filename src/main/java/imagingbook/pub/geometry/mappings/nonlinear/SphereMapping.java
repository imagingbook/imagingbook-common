/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.nonlinear;

import imagingbook.pub.geometry.mappings.Mapping;

public class SphereMapping extends Mapping {
	double xc;				// center of sphere
	double yc;
	double rad;				// radius of sphere
	double refIdx = 1.8;	// refraction index
   
	SphereMapping (double xc, double yc, double rad, boolean inv) {
		this.xc = xc;
		this.yc = yc;
		this.rad = rad;
//		this.isInverseFlag = inv;
	}
	
	public static SphereMapping makeInverseMapping(double xc, double yc, double rad){
		return new SphereMapping(xc, yc, rad, true);
	}

	public double[] applyTo (double x, double y){
		double dx = x-xc;
		double dy = y-yc;
		double dx2 = dx*dx;
		double dy2 = dy*dy;
		double rad2 = rad*rad;
		
		double r2 = dx*dx + dy*dy;
		
		if (r2 > 0 && r2 < rad2) {
			double z2 = rad2 - r2; 
			double z = Math.sqrt(z2);

			double xAlpha = Math.asin(dx / Math.sqrt(dx2 + z2));
			double xBeta = xAlpha - xAlpha * (1 / refIdx);
			double x1 = x - z * Math.tan(xBeta);

			double yAlpha = Math.asin(dy / Math.sqrt(dy2 + z2));
			double yBeta = yAlpha - yAlpha * (1 / refIdx);
			double y1 = y - z * Math.tan(yBeta);
			//pnt.setLocation(x1, y1);
			return new double[] {x1, y1};
		} 
		// otherwise leave point unchanged
		return new double[] {x, y};
	}
}




