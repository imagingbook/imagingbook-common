package imagingbook.pub.mser;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.pub.mser.MserDetector.Parameters;
import imagingbook.pub.mser.components.Component;
import imagingbook.pub.mser.components.ComponentTree.Method;

import static imagingbook.DATA.MserTestImage.*;

public class MserDetectorTest {

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
		
		runMser(Blob1, 3);
		runMser(Blob2, 6);
		runMser(Blob3, 9);
		runMser(BlobLevelTest, 2);
		runMser(BlobLevelTestNoise, 2);
		runMser(BlobOriented, 3);
		runMser(BlobsInWhite, 3);
		runMser(BoatsTiny, 21);
		runMser(BoatsTinyB, 22);
		runMser(BoatsTinyBW, 1);
		runMser(BoatsTinyW, 21);
		runMser(BoatsTinyW2, 22);
		runMser(AllBlack, 0);
		runMser(AllWhite, 0);
	}

	private void runMser(ImageResource res, int mserExpected) {
//		System.out.println("running " + res);
		ByteProcessor ip = (ByteProcessor) res.getImage().getProcessor();
		List<Component<MserData>> msers = detector.applyTo((ByteProcessor) ip);
//		System.out.println(msers.size());
		assertEquals("detected MSER components (" + res + ")", mserExpected, msers.size());
	}

}
