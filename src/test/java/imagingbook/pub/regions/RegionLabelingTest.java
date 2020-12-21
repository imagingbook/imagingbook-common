package imagingbook.pub.regions;

import static imagingbook.pub.regions.NeighborhoodType.N4;
import static imagingbook.pub.regions.NeighborhoodType.N8;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.DATA.images.Resources;
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.regions.BinaryRegionSegmentation.BinaryRegion;

public class RegionLabelingTest {
	
	private static class TestCase {
		final String imageName;
		final int regionCount_N4, regionCount_N8;
		
		TestCase(String imageName, int regionCount_N4, int regionCount_N8) {
			this.imageName = imageName;
			this.regionCount_N4 = regionCount_N4;
			this.regionCount_N8 = regionCount_N8;
		}
		
		int getRegionCount(NeighborhoodType nht) {
			return (nht == N4) ? regionCount_N4 : regionCount_N8;
		}
	}
	
	
	
//	static String ImgName = "marker-test-01a-bin.png";
//	static int RegionCount_N4 = 8;
//	static int RegionCount_N8 = 7;
	
	static String ImgName = "segmentation-small.png";
	static int RegionCount_N4 = 26;
	static int RegionCount_N8 = 9;
	
	TestCase TC1 = new TestCase("segmentation-small.png", 26, 9);
	
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
	public void testSegmentationRecursive() {
		run(new SegmentationRecursive(bp, N4), RegionCount_N4);
		run(new SegmentationRecursive(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationRegionContour() {
		//run(new SegmentationRegionContour(bp, N4), RegionCount_N4);
		run(new SegmentationRegionContour(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testXXX() {
		run(SegmentationBreadthFirst.class, TC1);
	}
	
	// ---------------------------------------------------------------
	
	private void run(Class<?> clazz, TestCase tc) {
		Constructor<?> constr = null;
		try {
			constr = clazz.getConstructor(ByteProcessor.class, NeighborhoodType.class);
		} catch (NoSuchMethodException | SecurityException e) { }
		Assert.assertNotNull(constr);
		
		Path path = new Resources().getResourcePath(tc.imageName);
		ByteProcessor bp = (ByteProcessor) IjUtils.openImage(path).getProcessor();
		Assert.assertNotNull(bp);
		
		for (NeighborhoodType nht : NeighborhoodType.values()) {
			BinaryRegionSegmentation labeling = null;
			try {
				labeling = (BinaryRegionSegmentation) constr.newInstance(bp, nht);
			} catch (Exception e) { }
			Assert.assertNotNull(labeling);
			List<BinaryRegion> regions = labeling.getRegions(true);
			Assert.assertNotNull(regions);
			Assert.assertEquals(tc.getRegionCount(nht), regions.size());
		}
	}
	
	private void run(BinaryRegionSegmentation labeling, int rc) {
		List<BinaryRegion> regions = labeling.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertEquals(rc, regions.size());
	}

}
