package imagingbook.pub.morphology;

import static imagingbook.lib.ij.IjUtils.match;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.data.TestData;

public class SkeletonTest {

	@Test
	public void test() {
		String origName = "cat.png";
		String resultName = "cat-skeleton.png";
		ImagePlus im1 = TestData.openImage(origName);
		assertNotNull("", im1);
		
		ImageProcessor ip1 = im1.getProcessor();
		
		BinaryMorphologyFilter bmf = new BinaryMorphologyFilter();
		int k = bmf.thin((ByteProcessor)ip1);
		assertEquals("thinning iterations expected", 12, k);
		
		ImagePlus im2 = TestData.openImage(resultName);
		assertNotNull("", im2);
		ImageProcessor ip2 = im2.getProcessor();
		
		// compare ip1 and ip2
		assertTrue("results must match", match(ip1, ip2));
	}

}
