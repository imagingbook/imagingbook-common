package imagingbook.pub.color.image;

import org.junit.Assert;
import org.junit.Test;

public class HsvConverterTest {

	@Test
			public void testFromRGB() {
				//
			}

	@Test
		public void testToRGB() {
			//
		}
	
	@Test
			public void testFromRGBtoRGB() {  // tests all 16 mio RGB colors
				HsvConverter hsvC = new HsvConverter();
				int[] rgb = new int[3];
				for (rgb[0] = 0; rgb[0] < 256; rgb[0]++) {
					for (rgb[1] = 0; rgb[1] < 256; rgb[1]++) {
						for (rgb[2] = 0; rgb[2] < 256; rgb[2]++) {
							float[] hsv = hsvC.fromRGB(rgb);
							Assert.assertArrayEquals(rgb, hsvC.toRGB(hsv));
						}
					}
				}
			}

}
