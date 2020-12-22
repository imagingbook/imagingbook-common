package imagingbook.pub.regions;

import static imagingbook.pub.regions.NeighborhoodType.N4;
import static imagingbook.pub.regions.NeighborhoodType.N8;

import java.nio.file.Path;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.DATA.images.Resources;
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.regions.BinaryRegionSegmentation.BinaryRegion;

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
	
	final Path path;
	final ByteProcessor bp;
	
	public BinaryRegionSegmentation2_Test() {
		path = new Resources().getResourcePath(ImgName);
		bp = (ByteProcessor) IjUtils.openImage(path).getProcessor();
		Assert.assertNotNull(bp);
	}

	@Test
	public void testSegmentationBreadthFirst() {
		run(new SegmentationBreadthFirst(bp, N4), RegionCount_N4);
		run(new SegmentationBreadthFirst(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationDepthFirst() {
		run(new SegmentationDepthFirst(bp, N4), RegionCount_N4);
		run(new SegmentationDepthFirst(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationSequential() {
		run(new SegmentationSequential(bp, N4), RegionCount_N4);
		run(new SegmentationSequential(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationRegionContour() {
		run(new SegmentationRegionContour(bp, N4), RegionCount_N4);
		run(new SegmentationRegionContour(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationRecursive() {
		run(new SegmentationRecursive(bp, N4), RegionCount_N4);
		run(new SegmentationRecursive(bp, N8), RegionCount_N8);
	}
	
	
	// ---------------------------------------------------------------
	
	private void run(BinaryRegionSegmentation labeling, int rc) {
		List<BinaryRegion> regions = labeling.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertEquals(rc, regions.size());
	}

}
