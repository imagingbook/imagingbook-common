/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.color.quantize;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;

/** 
 * This abstract class is used a the superclass for color quantizers.
 */
public interface ColorQuantizerOld {
	public abstract ByteProcessor quantize(ColorProcessor cp);
	public abstract int[] quantize(int[] origPixels);
	public abstract int countQuantizedColors();

}
