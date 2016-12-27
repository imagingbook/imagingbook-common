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

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an octree.
 * 
 * @author Wilbur
 */
public class Octree {
	
	private OctreeNode root = null;
	private final int maxLevel = 8;	// TODO: check!
//	private final int nColors;
	private int nLeaves;
	private List<OctreeNode>[] reducibleNodes;
	

	@SuppressWarnings("unchecked")
	Octree(int nColors) {
//		this.nColors = nColors;	// TODO: where is nColors used??
		this.nLeaves = 0;
		reducibleNodes = new List[maxLevel];
		for (int j = 0; j < maxLevel; j++) {
			reducibleNodes[j] = new ArrayList<OctreeNode>();
		}
	}

	boolean addColor(int[] color) {
		if (root == null) {
			root = new OctreeNode(this,0);
		}
		return root.addColor(color);
	}
	
	public int getNumberOfLeaves() {
		return nLeaves;
	}
	
	public void addToLeaves(int count) {
		nLeaves = nLeaves + count;
	}
	
	/*
	 * OctreeNode specifies a single node inside the octree 
	 */
	private static class OctreeNode {
		private int level;
		private Octree root;
		private boolean isLeaf = false;
		private OctreeNode[] children;
//		private int paletteIndex;
		private int nPixels;
		private int redSum;
		private int grnSum;
		private int bluSum;
		
		private OctreeNode(Octree root, int depth) {
			this.root = root;
			this.level = depth;
			this.nPixels = 0;
			this.redSum = 0;
			this.grnSum = 0;
			this.bluSum = 0;
			this.children = null;
			if (level == root.maxLevel) {
				this.isLeaf = true;
				root.addToLeaves(1);
			}
			else {
				this.isLeaf = false;
				root.reducibleNodes[level].add(this);
			}
		}
		
//		private boolean isLeaf() {
//			return isLeaf;
//		}
		
		boolean addColor(int[] color) {
			if (isLeaf) {// this is a leaf node
				nPixels = nPixels + 1;
				redSum = redSum + color[0];
				grnSum = grnSum + color[1];
				bluSum = bluSum + color[2];
				return false;
			}
			else {	// not a leaf node, pass color on to children
				// create children is none exist yet
				if (children == null) {	// create children array if needed
					children = new OctreeNode[8];
				}
				// determine which child the color belongs to at this level
				int k = root.getBranchIdx(color, level);
				// create this particular child if not existant
				if (children[k] == null) {
					children[k] = new OctreeNode(root, level+1);
					children[k].addColor(color);
					return true;	// new color
				}
				else
					return children[k].addColor(color); // recursive return value 
			}
		}

		// reduce this tree node and return the resulting number of 
		// eliminated nodes
		public int reduce() {
			if (isLeaf) {
				root.addToLeaves(-1);
//				throw new Error("Attempt to reduce an empty node!");
//				IJ.write("Attempt to reduce an empty node!"); return 0;
			}
			else {
				// reduce a non-leaf node
				redSum = 0; grnSum = 0; bluSum = 0;
//				for (int k=0; k<children.length; k++) {
				for (OctreeNode child : children) {
//					OctreeNode child = children[k];
					if (child != null) {
						// recursively reduce all deeper nodes first
						child.reduce();
//						nChildren++;
						nPixels+= child.nPixels;
						redSum+= child.redSum;
						grnSum+= child.grnSum;
						bluSum+= child.bluSum;
					}
				}
				root.reducibleNodes[level].remove(this);
			}
			IJ.log("  reducing node at level " + this.level + " pixels:" + this.nPixels);
			children = null;
			isLeaf = true;
			// remove this node from the list of reducibles
//			root.addToLeaves(-nChildren);
			return (0);
		}

		public int countLeaves() {
			if (isLeaf)
				return 1;
			if (children == null)
				return 0;
			int count = 0;
			for (int j=0; j<children.length; j++) {
				if (children[j] != null)
					count = count + children[j].countLeaves();
			}
			return count;
		}
	}

	/*
	 * Compute the child index for color at a given depth
	 */
	public int getBranchIdx(int[] color, int depth) {
		int shift = maxLevel - depth;
		int index =
			(((color[0] >> shift) & 1) << 2) |	// red
			(((color[1] >> shift) & 1) << 1) |	// green
			 ((color[2] >> shift) & 1);			// blue
		return index;
	}

	// reduce this octree
	// returns the number of leaf nodes reduced
	public int reduce() {
		OctreeNode node = findReducibleNode();
		if (node == null)
			return 0;
		else {
			return node.reduce();
		}
	}
	
	void dumpReducibleNodes() {
		for (int j = maxLevel-1; j >= 0; j--) {
			List<OctreeNode> nodelist = reducibleNodes[j];
			IJ.log(" --- Nodes List at level " + j + ": " + nodelist.size());
		}
	}

	private OctreeNode findReducibleNode() {
		OctreeNode theNode = null;
		for (int j = maxLevel-1; j >= 0; j--) {
			IJ.log(" -> searching reducible node at level "+ j);
			List<OctreeNode> nodelist = reducibleNodes[j];
			if (!nodelist.isEmpty()) {
				//theNode = nodelist.get(0);	// needs improvement
				theNode = findMaxPixelNode(nodelist);
				break;
			}
		}
		IJ.log(" -> found reducable node at level " + theNode.level);
		return theNode;
	}
	
	private OctreeNode findMaxPixelNode(List<OctreeNode> nodelist) {
		OctreeNode maxNode = null;
		int maxCount = -1;
		for (OctreeNode node: nodelist) {
			int count = node.nPixels;
			if (count > maxCount) {
				maxCount = count;
				maxNode = node;
			}
		}
		return maxNode;
	}
	
	public int countLeaves() {
		return root.countLeaves();
	}
	

}
