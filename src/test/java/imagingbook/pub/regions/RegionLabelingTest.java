package imagingbook.pub.regions;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.DATA.images.Resources;
import imagingbook.lib.ij.IjUtils;

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
		run(new BreadthFirstLabeling(bp), NeighborhoodType.N4, RegionCount_N4);
		run(new BreadthFirstLabeling(bp), NeighborhoodType.N8, RegionCount_N8);
	}
	
	@Test
	public void testDepthFirstLabeling() {
		run(new DepthFirstLabeling(bp), NeighborhoodType.N4, RegionCount_N4);
		run(new DepthFirstLabeling(bp), NeighborhoodType.N8, RegionCount_N8);
	}
	
	@Test
	public void testSequentialLabeling() {
		run(new SequentialLabeling(bp), NeighborhoodType.N4, RegionCount_N4);
		run(new SequentialLabeling(bp), NeighborhoodType.N8, RegionCount_N8);
	}
	
		
//		labeling = new RegionContourLabeling((ByteProcessor) ip);
//		labeling = new RecursiveLabeling((ByteProcessor) ip);

	
	private void run(RegionLabeling labeling, NeighborhoodType nht, int rc) {
		labeling.neighborhood = nht;
		Assert.assertTrue(labeling.segment());
		Assert.assertEquals(rc, labeling.getRegions().size());
	}

}
