package imagingbook.DATA;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.resource.ResourceLocation;
import imagingbook.lib.util.resource.ResourceLocation.Resource;


public class GeneralResourcesTest {
	
	@Test
	public void testResourcesBasic() {
		String name = "boats.png";
		Resource res = null;
		
		ResourceLocation loc = new imagingbook.DATA.images.RLOC();
		
		res = loc.getResource(name);
		assertNotNull("existing resource not found" + name, res);
		
		res = loc.getResource("nonexistant");
		assertNull("nonexisting resource found: " + name, res);
		
		// check if all listed resource names really exist
		String[] names = loc.getResourceNames();
		for (String n : names) {
			assertNotNull("listed resource not found: " + name, loc.getResource(n));
		}
	}
	
	@Test
	public void openBoatsImage() {
		String name = "boats.png";
		
		ResourceLocation loc = new imagingbook.DATA.images.RLOC();
		Resource res = loc.getResource(name);
		assertNotNull("image resource not found" + name, res);
		
		ImagePlus im = res.openAsImage();
		assertNotNull(im);
		ImageProcessor ip = im.getProcessor();
		assertNotNull(ip);
		assertTrue("ByteProcessor expected", ip instanceof ByteProcessor);
		assertEquals(720, ip.getWidth());
		assertEquals(8, ip.getBitDepth());
		
		im.flush();
	}


}
