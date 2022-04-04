package imagingbook.pub.mser;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.lib.util.resource.ResourceLocation;
import imagingbook.lib.util.resource.ResourceLocation.Resource;
import imagingbook.pub.mser.MserDetector.Parameters;
import imagingbook.pub.mser.components.Component;
import imagingbook.pub.mser.components.ComponentTree.Method;

public class MserDetectorTest {

	static ResourceLocation loc = new imagingbook.DATA.mserTestImages.RLOC();

	private Parameters params = null;
	MserDetector detector = null;

	/**
	 * Runs validation on component trees from different images.
	 */
	@Test
	public void test1() {
		params = new Parameters();	// MSER default parameters

		params.method = Method.LinearTime;
		params.delta = 5;
		params.minAbsComponentArea = 	3;
		params.minRelCompSize = 	0.0001;
		params.maxRelCompSize = 	0.25;
		params.maxSizeVariation = 		0.25;
		params.minDiversity = 			0.50;
		params.constrainEllipseSize = 	true;
		params.minCompactness = 		0.2;		
		params.validateComponentTree =	false;

		detector = new MserDetector(params);

		//System.out.println(Arrays.toString(loc.getResourceNames()));
		run1("blob1.png", 3);
		run1("blob2.png", 6);
		run1("blob3.png", 9);
		run1("blob-level-test.png", 2);
		run1("blob-level-test-noise.png", 2);
		run1("blob-oriented.png", 3);
		run1("blobs-in-white.png", 3);
		run1("boats-tiny.png", 21);
		run1("boats-tiny-b.png", 22);
		run1("boats-tiny-bw.png", 1);
		run1("boats-tiny-w.png", 21);
		run1("boats-tiny-w2.png", 22);
		run1("all-black.png", 0);
		run1("all-white.png", 0);
	}

	private void run1(String name, int mserExpected) {
		Resource resource = loc.getResource(name);
		ByteProcessor ip = (ByteProcessor) resource.openAsImage().getProcessor();
		List<Component<MserData>> msers = detector.applyTo((ByteProcessor) ip);
		//System.out.println(msers.size());
		assertEquals("detected MSER components (" + name + ")", mserExpected, msers.size());
	}

}
