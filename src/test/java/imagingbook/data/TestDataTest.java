package imagingbook.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.ResourceUtils;



public class TestDataTest {

//	@Test
//	public void listResources() {
//		Path dir = ResourceUtils.getResourcePath(TestData.class, "images");
//		assertNotNull("resource directory not found", dir);
//		System.out.println("uri = " + dir);
//		Path[] paths = ResourceUtils.getResourcePaths(dir);
//		for (Path p : paths) {
//			System.out.println(" - " + p.getFileName().toString());
//		}
//	}
	
	@Test
	public void openBoatsImage() {
		String name = "boats.tif";
		Path path = null;
		
		path = ResourceUtils.getResourcePath(TestData.class, "images/" + name);
		assertNotNull("image resource not found (1): " + name, path);
		
		path = TestData.getResourcePath("images", name);
		assertNotNull("image resource not found (2): " + name, path);
		
		// preferred!
		path = TestData.getImagePath(name);
		assertNotNull("image resource not found (3): " + name, path);
		
		// preferred!
		ImagePlus im = TestData.openImage(name); // alternative: new Opener().openImage(path.toString());
		ImageProcessor ip = im.getProcessor();
		assertNotNull(ip);
		assertTrue("ByteProcessor expected", ip instanceof ByteProcessor);
		assertEquals(720, ip.getWidth());
		assertEquals(8, ip.getBitDepth());
		
		im.flush();
	}

}
