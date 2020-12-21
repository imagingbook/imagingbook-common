package imagingbook.pub.regions;

import ij.process.ByteProcessor;

/**
 * TODO: This is temporary and will change!
 * @author WB
 * @deprecated
 */
public enum LabelingMethod {
	
	BreadthFirst, 
	DepthFirst, 
	Recursive, 
	RegionAndContours,
	Sequential;
	
	public static BinaryRegionSegmentation getInstance(LabelingMethod method, ByteProcessor bp, NeighborhoodType nht) {
		BinaryRegionSegmentation segmenter = null;
		switch (method) {
			case BreadthFirst:		segmenter = new SegmentationBreadthFirst(bp, nht); break;
			case DepthFirst:		segmenter = new SegmentationDepthFirst(bp, nht); break;
			case Recursive:			segmenter = new SegmentationRecursive(bp, nht); break; 
			case RegionAndContours:	segmenter = new SegmentationRegionContour(bp, nht); break;
			case Sequential:		segmenter = new SegmentationSequential(bp, nht); break;
		}
		return segmenter;
	}
}