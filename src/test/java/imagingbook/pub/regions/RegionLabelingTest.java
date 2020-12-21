package imagingbook.pub.regions;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.DATA.images.Resources;
import imagingbook.lib.ij.IjUtils;

import static imagingbook.pub.regions.NeighborhoodType.*;

public class RegionLabelingTest {
	
	static String ImgName = "marker-test-01a-bin.png";
	static int RegionCount_N4 = 8;
	static int RegionCount_N8 = 7;
	
	final Path path;
	final ImageProcessor ip;
	final ByteProcessor bp;
	
	public RegionLabelingTest() {
		path = new Resources().getResourcePath(ImgName);
		ip = IjUtils.openImage(path).getProcessor();
		bp = (ByteProcessor) ip;
		Assert.assertNotNull(bp);
	}

	@Test
	public void testBreadthFirstLabeling() {
		run(new SegmentationBreadthFirst(bp, N4), RegionCount_N4);
		run(new SegmentationBreadthFirst(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testDepthFirstLabeling() {
		run(new SegmentationDepthFirst(bp, N4), RegionCount_N4);
		run(new SegmentationDepthFirst(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSequentialLabeling() {
		run(new SegmentationSequential(bp, N4), RegionCount_N4);
		run(new SegmentationSequential(bp, N8), RegionCount_N8);
	}
	
		
//		labeling = new RegionContourLabeling((ByteProcessor) ip);
//		labeling = new RecursiveLabeling((ByteProcessor) ip);

	
	private void run(BinaryRegionSegmentation labeling, int rc) {
		//Assert.assertTrue(labeling.segment());
		Assert.assertEquals(rc, labeling.getRegions(true).size());
	}

}
