package imagingbook.pub.regions;

import ij.process.ByteProcessor;

/**
 * TODO: This is temporary and will change!
 * @author WB
 *
 */
public enum LabelingMethod {
	
	BreadthFirst, 
	DepthFirst, 
	Recursive, 
	RegionAndContours,
	Sequential;
	
	public static RegionLabeling getInstance(LabelingMethod method, ByteProcessor bp) {
		RegionLabeling segmenter = null;
		switch (method) {
			case BreadthFirst:		segmenter = new BreadthFirstLabeling(bp); break;
			case DepthFirst:		segmenter = new DepthFirstLabeling(bp); break;
			case Recursive:			segmenter = new RecursiveLabeling(bp); break; 
			case RegionAndContours:	segmenter = new RegionContourLabeling(bp); break;
			case Sequential:		segmenter = new SequentialLabeling(bp); break;
		}
		return segmenter;
	}
}