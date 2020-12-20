package imagingbook.pub.regions;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;


public class BreadthFirstLabelingTest {
	
	String imgName = "marker-test-01a-bin.png";
	int imgRegionCount = 8;

	@Test
	public void test() {
		Path path = new imagingbook.DATA.images.Resources().getResourcePath(imgName);
		ImageProcessor ip = IjUtils.openImage(path).getProcessor();
		Assert.assertTrue(ip instanceof ByteProcessor); 
		
		RegionLabeling labeling = null;
		
		labeling = new BreadthFirstLabeling((ByteProcessor) ip);
		Assert.assertTrue(labeling.segment());
		Assert.assertEquals(imgRegionCount, labeling.getRegions().size());
		
		labeling = new DepthFirstLabeling((ByteProcessor) ip);
		Assert.assertTrue(labeling.segment());
		Assert.assertEquals(imgRegionCount, labeling.getRegions().size());
		
//		labeling = new RegionContourLabeling((ByteProcessor) ip);
//		Assert.assertTrue(labeling.segment());
//		Assert.assertEquals(imgRegionCount, labeling.getRegions().size());
		
//		labeling = new RecursiveLabeling((ByteProcessor) ip);
//		Assert.assertTrue(labeling.segment());
//		Assert.assertEquals(imgRegionCount, labeling.getRegions().size());
	}

}
