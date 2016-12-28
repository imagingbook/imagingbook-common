/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.color.quantize;

import ij.IJ;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;

/**
 * The principle of the octree algorithm is to sequentially read in the image. 
 * Every color is then stored in an octree of depth 8 (every leaf at depth 8 
 * represents a distinct color). A limit of K (in this case K = 256) leaves 
 * is placed on the tree. Insertion of a color in the tree can result in two 
 * outcomes:
 * 
 * (A) If the tree has fewer than K leaves, the color is filtered down the tree 
 * until either it reaches some leaf node that has an associated representative 
 * color or it reaches the leaf node representing its unique color.
 * 
 * (B) If there are more than K leaves in the tree some set of leaves in the tree 
 * must be merged (their representative colors averaged) together and a new representative 
 * color stored in their parent. 
 * 
 * Gervautz and Purgathofer [1] offer 2 possible criteria to be used in the selection 
 * of leaves to be merged:
 * Reducible nodes that have the largest depth in the tree should be chosen first.
 * They represent colors that lie closest together.
 * 
 * If there is more than one group of leaves at the maximum depth the algorithm could:
 * 1. Merge the leaves that represent the fewest number of pixels. This will help 
 * keep the error small
 * 2. Reduce the leaves that represent the most pixels. In this case large areas 
 * will be uniformly filled in a slightly wrong color while maintaining detailed 
 * shadings.
 * 
 * Once the entire image has been processed in this manner the color map consists 
 * of the representative colors of the leaf nodes in the tree. The index of the color 
 * map is then stored at that leaf, and the process of quantizing the image is simply 
 * filtering each color down the tree until a leaf is hit.
 * 
 * Because a limit is placed on the number of leaves in the tree this algorithm has 
 * a modest memory complexity, O(K), compared to the median cut and popularity algorithms. 
 * The time complexity is less clear. Gervautz and Purgathofer [1] cite the search phase 
 * as being O(N), where N is the number of pixels in the image. This is clearly best case 
 * behavior. The average case needs to address the complexity of the merging algorithm.
 * 
 * TODO: make a subclass of ColorQuantizer, add comments...
 * 
 * ATTENTION: Current implementation is non-functional!
 * @deprecated
 */
public class OctreeQuantizer { // extends ColorQuantizer {
	
	private Octree tree;
	private ColorProcessor ip;
	private int nColors = 256;
	
	// ----------------------------------------------------------

	public OctreeQuantizer (ColorProcessor ip, int nColors) {
		this.ip = ip;
		this.nColors = nColors;
		buildOctree();
	}
	
	public OctreeQuantizer (ColorProcessor ip) {
		this(ip, 256);
	}
	
	// ----------------------------------------------------------
	
	public ByteProcessor quantize(ColorProcessor ip) {
		return null;
	}
	
	public int getNumberOfLeaves() {
		if (tree == null)
			return 0;
		else
			return tree.getNumberOfLeaves();
	}
	
	//----------------------------------------------------------------------
	
	private void buildOctree() {
		tree = new Octree(nColors);
		int[] pixels = (int[]) ip.getPixels();
		int[] rgb = new int[3];
		int newColors = 0;

		for (int i = 0; i < pixels.length; i++) {
			int c = pixels[i];
			rgb[0] = (c & 0xff0000) >> 16;
			rgb[1] = (c & 0xff00) >> 8;
			rgb[2] = (c & 0xff);
			boolean newColor = tree.addColor(rgb);
			IJ.log("  " + i + " adding " + c + " " + newColor);
			if (!newColor)
				IJ.log("************* existing color!");
			IJ.log("   Leaves: " + tree.getNumberOfLeaves() + " counted:" + tree.countLeaves());
			// tree.dumpReducibleNodes();
			// if (newColor) newColors++;
			if (tree.getNumberOfLeaves() >= nColors) {
				IJ.log("reducing tree");
				int nReduced = tree.reduce();
				IJ.log("reduced by " + nReduced + " nodes.");
			}
			if (tree.getNumberOfLeaves() > nColors) {
				IJ.log("*** Still too many leaves: " + tree.getNumberOfLeaves());
			}
		}
		IJ.log("New colors: " + newColors);
	}
	
	//----------------------------------------------------------------------


}
