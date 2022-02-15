package imagingbook.pub.color.image;

import static imagingbook.pub.color.image.Illuminant.D50;
import static imagingbook.pub.color.image.Illuminant.D65;
import static org.junit.Assert.assertArrayEquals;

import java.awt.color.ColorSpace;
import java.util.Random;

import org.junit.Test;

public class XYZscalingAdaptationTest {

	@Test
	public void test() {
		ChromaticAdaptation adapt65_50 = new XYZscalingAdaptation(D65, D50);	// adapts from D65 -> D50
		ChromaticAdaptation adapt50_65 = new XYZscalingAdaptation(D50, D65);	// adapts from D50 -> D65
		
		ColorSpace cs = new sRgb65ColorSpace();
		Random rg = new Random(17);
		float[] rgb = new float[3];
		
		for (int i = 0; i < 1000; i++) {
			rgb[0] = rg.nextFloat();
			rgb[1] = rg.nextFloat();
			rgb[2] = rg.nextFloat();
			
			float[] XYZ65a = cs.toCIEXYZ(rgb);
			float[] XYZ50 =  adapt65_50.applyTo(XYZ65a);
			float[] XYZ65b = adapt50_65.applyTo(XYZ50);
			
			assertArrayEquals(XYZ65a, XYZ65b, 0.00001f);
		}
	}

}