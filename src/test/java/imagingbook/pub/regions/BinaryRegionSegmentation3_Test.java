package imagingbook.pub.regions;

import static imagingbook.pub.geometry.basic.NeighborhoodType2D.N4;
import static imagingbook.pub.geometry.basic.NeighborhoodType2D.N8;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.DATA.images.RLOC;
import imagingbook.lib.util.resource.ResourceLocation.Resource;
import imagingbook.pub.regions.segment.BinaryRegionSegmentation;
import imagingbook.pub.regions.segment.BreadthFirstSegmentation;
import imagingbook.pub.regions.segment.DepthFirstSegmentation;
import imagingbook.pub.regions.segment.RegionContourSegmentation;
import imagingbook.pub.regions.segment.SequentialSegmentation;

/**
 * Segmentation test on a medium-sized image which cannot be handled by 
 * the recursive method.
 * 
 * @author WB
 */
public class BinaryRegionSegmentation3_Test {
	
	static String ImgName = "segmentation-med.png";
	static int RegionCount_N4 = 88;
	static int RegionCount_N8 = 9;
	
	final Resource path;
	final ByteProcessor bp;
	
	public BinaryRegionSegmentation3_Test() {
		path = new RLOC().getResource(ImgName);
		bp = (ByteProcessor) path.openAsImage().getProcessor();
		Assert.assertNotNull(bp);
	}

	@Test
	public void testSegmentationBreadthFirst() {
		run(new BreadthFirstSegmentation(bp, N4), RegionCount_N4);
		run(new BreadthFirstSegmentation(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationDepthFirst() {
		run(new DepthFirstSegmentation(bp, N4), RegionCount_N4);
		run(new DepthFirstSegmentation(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationSequential() {
		run(new SequentialSegmentation(bp, N4), RegionCount_N4);
		run(new SequentialSegmentation(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationRegionContour() {
		run(new RegionContourSegmentation(bp, N4), RegionCount_N4);
		run(new RegionContourSegmentation(bp, N8), RegionCount_N8);
	}
	
//	@Test
//	public void testSegmentationRecursive() {
//		run(new SegmentationRecursive(bp, N4), RegionCount_N4);
//		run(new SegmentationRecursive(bp, N8), RegionCount_N8);
//	}
	
	// ---------------------------------------------------------------
	
	private void run(BinaryRegionSegmentation labeling, int rc) {
		List<BinaryRegion> regions = labeling.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertEquals(rc, regions.size());
	}

}
