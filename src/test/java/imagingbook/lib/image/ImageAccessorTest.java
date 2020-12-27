package imagingbook.lib.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;
import java.util.Random;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;


public class ImageAccessorTest {

	@Test
	public void testRandomScalarValues() {
		String name = "boats.tif";
		Path path = new imagingbook.DATA.images.Resources().getResourcePath("boats.tif");
		assertNotNull("resource not found" + name, path);
		ImagePlus im = IjUtils.openImage(path);
		ImageProcessor ip = im.getProcessor();
		assertNotNull(ip);
		
		run(ip.convertToByteProcessor());
		run(ip.convertToShortProcessor());
		run(ip.convertToFloatProcessor());
		
		im.close();
	}
	
	private void run(ImageProcessor ip) {
		ImageAccessor.Scalar ia = ImageAccessor.Scalar.create(ip, null, null);
		int width = ia.getWidth();
		int height = ia.getHeight();
		Random rd = new Random(17);
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				float v1 = (int)(rd.nextFloat() * 255);
				ia.setVal(u, v, v1);
				float v2 = ia.getVal(u, v);
				assertEquals(v1, v2, 1E-6F);
			}
		}
	}

}
