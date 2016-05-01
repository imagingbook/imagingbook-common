/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap23_Bildvergleich;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.matching.DistanceTransform;
import imagingbook.pub.matching.DistanceTransform.Norm;

/**
 * Demonstrates the use of the DistanceTransform class.
 * @author W. Burger
 * @version 2014-04-20
 */
public class Show_Distance_Map implements PlugInFilter {
	
	ImagePlus img;
	
    public int setup(String arg, ImagePlus imp) {
    	this.img = imp;
        return DOES_8G + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
    	DistanceTransform dt = new DistanceTransform(ip, Norm.L2);
		FloatProcessor dtIp = new FloatProcessor(dt.getDistanceMap());
		(new ImagePlus("Distance Transform of " + img.getTitle(), dtIp)).show();
    }
		
}
