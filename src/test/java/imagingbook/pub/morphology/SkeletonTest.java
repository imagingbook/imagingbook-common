package imagingbook.pub.morphology;

import static imagingbook.lib.ij.IjUtils.match;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.util.resource.ResourceLocation;

public class SkeletonTest {
	
	private String origName = "cat.png";
	private String resultName = "cat-skeleton.png";
	private ResourceLocation loc = new imagingbook.DATA.images.Resources();

	@Test
	public void test() {
		
		Path origPath   = loc.getPath(origName);
		assertNotNull(origPath);
		
		Path resultPath = loc.getPath(resultName);
		assertNotNull(resultPath);
		
		ImagePlus origIm = IjUtils.openImage(origPath);
		assertNotNull(origIm);		
		ImageProcessor origIp = origIm.getProcessor();
		
		BinaryMorphologyFilter morph = new BinaryMorphologyFilter();
		int k = morph.thin((ByteProcessor)origIp);
		assertEquals("thinning iterations expected", 12, k);
		
		ImagePlus resultIm = IjUtils.openImage(resultPath);
		assertNotNull(resultIm);
		ImageProcessor resultIp = resultIm.getProcessor();
		
		// compare ip1 and ip2
		assertTrue("results must match", match(origIp, resultIp));
	}

}
