package imagingbook.DATA;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.util.ResourceLocation;


public class GeneralResourcesTest {
	
	@Test
	public void testResourcesBasic() {
		String name = "boats.png";
		Path path = null;
		
		ResourceLocation loc = new imagingbook.DATA.images.Resources();
		
		path = loc.getResourcePath(name);
		assertNotNull("existing resource not found" + name, path);
		
		path = loc.getResourcePath("nonexistant");
		assertNull("nonexisting resource found: " + name, path);
		
		// check if all listed resource names really exist
		String[] names = loc.getResourceNames();
		for (String n : names) {
			assertNotNull("listed resource not found: " + name, loc.getResourcePath(n));
		}
	}
	
	@Test
	public void openBoatsImage() {
		String name = "boats.png";
		
		ResourceLocation loc = new imagingbook.DATA.images.Resources();
		Path path = loc.getResourcePath(name);
		assertNotNull("image resource not found" + name, path);
		
		ImagePlus im = IjUtils.openImage(path);
		ImageProcessor ip = im.getProcessor();
		assertNotNull(ip);
		assertTrue("ByteProcessor expected", ip instanceof ByteProcessor);
		assertEquals(720, ip.getWidth());
		assertEquals(8, ip.getBitDepth());
		
		im.flush();
	}


}
