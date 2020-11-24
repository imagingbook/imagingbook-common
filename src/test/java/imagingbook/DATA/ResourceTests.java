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


public class ResourceTests {
	
	@Test
	public void testResourcesBasic() {
		String name = "boats.tif";
		Path path = null;
		
		ResourceLocation rd = new imagingbook.DATA.images.Resources();
		
		path = rd.getResourcePath(name);
		assertNotNull("existing resource not found" + name, path);
		
		path = rd.getResourcePath("nonexistant");
		assertNull("nonexisting resource found: " + name, path);
		
		// check if all listed resource names really exist
		String[] names = rd.getResourceNames();
		for (String n : names) {
			assertNotNull("listed resource not found: " + name, rd.getResourcePath(n));
		}
	}
	
	@Test
	public void openBoatsImage() {
		String name = "boats.tif";
		
		ResourceLocation rd = new imagingbook.DATA.images.Resources();
		Path path = rd.getResourcePath(name);
		assertNotNull("image resource not found" + name, path);
		
		ImagePlus im = IjUtils.openImage(path);
		ImageProcessor ip = im.getProcessor();
		assertNotNull(ip);
		assertTrue("ByteProcessor expected", ip instanceof ByteProcessor);
		assertEquals(720, ip.getWidth());
		assertEquals(8, ip.getBitDepth());
		
		im.flush();
	}
	
//	@Test
//	public void fooTest() {
//		ResourceLocation rd = new imagingbook.data.images.DataDir();
//		Path path = rd.getResourcePath("DataDir.class");
//		System.out.println(path);
//		String[] names = rd.getResourceNames();
//		for (String p : names) {
//			System.out.println(p);
//		}
//		System.out.println(rd.getResourcePath(names[0]));
//	}

}
