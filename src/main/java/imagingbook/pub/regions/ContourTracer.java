/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package imagingbook.pub.regions;

import java.util.List;


/*
 * Added 2014-11-12
 */
public interface ContourTracer {

	public List<Contour> getInnerContours();
	public List<Contour> getInnerContours(boolean sort);
	public List<Contour> getOuterContours();
	public List<Contour> getOuterContours(boolean sort);
	
}
