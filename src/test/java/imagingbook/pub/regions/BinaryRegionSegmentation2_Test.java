package imagingbook.pub.regions;

import static imagingbook.pub.regions.NeighborhoodType.N4;
import static imagingbook.pub.regions.NeighborhoodType.N8;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.DATA.images.RLOC;
import imagingbook.lib.util.resource.ResourceLocation.Resource;
import imagingbook.pub.regions.segment.BinaryRegionSegmentation;
import imagingbook.pub.regions.segment.BreadthFirstSegmentation;
import imagingbook.pub.regions.segment.DepthFirstSegmentation;
import imagingbook.pub.regions.segment.RecursiveSegmentation;
import imagingbook.pub.regions.segment.RegionContourSegmentation;
import imagingbook.pub.regions.segment.SequentialSegmentation;
import imagingbook.pub.regions.segment.BinaryRegionSegmentation.BinaryRegion;

/**
 * Segmentation test on a small image  which can also be handled by the
 * recursive method.
 * 
 * @author WB
 */
public class BinaryRegionSegmentation2_Test {
	
	static String ImgName = "cat-skeleton.png";
	static int RegionCount_N4 = 46;
	static int RegionCount_N8 = 1;
	
	final Resource path;
	final ByteProcessor bp;
	
	public BinaryRegionSegmentation2_Test() {
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
	
	@Test
	public void testSegmentationRecursive() {
		run(new RecursiveSegmentation(bp, N4), RegionCount_N4);
		run(new RecursiveSegmentation(bp, N8), RegionCount_N8);
	}
	
	
	// ---------------------------------------------------------------
	
	private void run(BinaryRegionSegmentation labeling, int rc) {
		List<BinaryRegion> regions = labeling.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertEquals(rc, regions.size());
	}

}
