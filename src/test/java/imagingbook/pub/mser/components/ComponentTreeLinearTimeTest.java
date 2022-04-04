package imagingbook.pub.mser.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.lib.util.resource.ResourceLocation;
import imagingbook.lib.util.resource.ResourceLocation.Resource;
import imagingbook.pub.mser.components.ComponentTree.Method;

public class ComponentTreeLinearTimeTest {

	static ResourceLocation loc = new imagingbook.DATA.mserTestImages.RLOC();
	
//	static String[] files = {
//			"blob1.png",
//			"blob2.png",
//			"blob3.png",
//			"blob-level-test.png",
//			"blob-level-test-noise.png",
//			"blob-oriented.png",
//			"blobs-in-white.png",
//			"boats-tiny.png",
//			"boats-tiny-b.png",
//			"boats-tiny-bw.png",
//			"boats-tiny-w.png",
//			"boats-tiny-w2.png",
//			"all-black.png",
//			"all-white.png"
//		};
	
	/**
	 * Runs validation on component trees from different images.
	 */
	@Test
	public void test1() {
		//System.out.println(Arrays.toString(loc.getResourceNames()));
		run1("blob1.png");
		run1("blob2.png");
		run1("blob3.png");
		run1("blob-level-test.png");
		run1("blob-level-test-noise.png");
		run1("blob-oriented.png");
		run1("blobs-in-white.png");
		run1("boats-tiny.png");
		run1("boats-tiny-b.png");
		run1("boats-tiny-bw.png");
		run1("boats-tiny-w.png");
		run1("boats-tiny-w2.png");
		run1("all-black.png");
		run1("all-white.png");
	}

	private void run1(String name) {
		Resource resource = loc.getResource(name);
		ByteProcessor ip = (ByteProcessor) resource.openAsImage().getProcessor();
		ComponentTree<?> ct = ComponentTree.from(ip, Method.LinearTime); //new ComponentTreeLinear<>(ip);
		Assert.assertTrue("component tree validation failed: " + name, ct.validate());
	}
	
	// ----------------------------------------------------------

	/**
	 * Checks the expected number of components and leaves on different
	 * images under rotation and reflection.
	 */
	@Test
	public void test2() {
		run2("blob1.png", 6, 1);
		run2("blob2.png", 11, 2);
		run2("blob3.png", 32, 8);
		run2("blob-level-test.png", 3, 2);
		run2("blob-level-test-noise.png", 341, 159);
		run2("blob-oriented.png", 10, 1);
		run2("blobs-in-white.png", 4, 3);
		run2("boats-tiny.png", 312, 70);
		run2("boats-tiny-b.png", 309, 69);
		run2("boats-tiny-bw.png", 3, 2);
		run2("boats-tiny-w.png", 313, 70);
		run2("boats-tiny-w2.png", 312, 69);
		run2("all-black.png", 1, 1);
		run2("all-white.png", 1, 1);
	}
	
	
	private void run2(String name, int noComponents, int noLeaves) {
		Resource resource = loc.getResource(name);
		ByteProcessor ip = (ByteProcessor) resource.openAsImage().getProcessor();
		
		for (int i = 0; i < 2; i++) {	// 2 rotations
			check2(ip, noComponents, noLeaves);
			ip.flipHorizontal();
			check2(ip, noComponents, noLeaves);
			ip.flipVertical();
			check2(ip, noComponents, noLeaves);
			ip.flipHorizontal();
			check2(ip, noComponents, noLeaves);
			
			ip = (ByteProcessor) ip.rotateLeft();
		}
	}
	
	private void check2(ByteProcessor ip, int noCompomenents, int noLeaves) {
		ComponentTree<?> ct = ComponentTree.from(ip, Method.LinearTime); //new ComponentTreeLinear<>(ip);
		
		Component<?> root = ct.getRoot();
		assertNotNull(root);
		int imgSize = ip.getWidth() * ip.getHeight();
		assertEquals("checking size of root component", root.getSize(), imgSize);
		
		Collection<? extends Component<?>> components = ct.getComponents();
		assertEquals("checking expected total number of components", noCompomenents, components.size());
		
		Collection<? extends Component<?>> leaves = ct.getLeaves();
		assertEquals("checking expected number of leaf components", noLeaves, leaves.size());
	}

}
