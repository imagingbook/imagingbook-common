package imagingbook.pub.morphology;

import static imagingbook.lib.ij.IjUtils.match;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.resource.ResourceLocation;
import imagingbook.lib.util.resource.ResourceLocation.Resource;

public class SkeletonTest {
	
	private String origName = "cat.png";
	private String resultName = "cat-skeleton.png";
	private ResourceLocation loc = new imagingbook.DATA.images.RLOC();

	@Test
	public void test() {
		
		Resource origPath   = loc.getResource(origName);
		assertNotNull(origPath);
		
		Resource resultPath = loc.getResource(resultName);
		assertNotNull(resultPath);
		
		ImagePlus origIm = origPath.openAsImage();
		assertNotNull(origIm);		
		ImageProcessor origIp = origIm.getProcessor();
		
		BinaryMorphologyFilter morph = new BinaryMorphologyFilter();
		int k = morph.thin((ByteProcessor)origIp);
		assertEquals("thinning iterations expected", 12, k);
		
		ImagePlus resultIm = resultPath.openAsImage();
		assertNotNull(resultIm);
		ImageProcessor resultIp = resultIm.getProcessor();
		
		// compare ip1 and ip2
		assertTrue("results must match", match(origIp, resultIp));
	}

}
