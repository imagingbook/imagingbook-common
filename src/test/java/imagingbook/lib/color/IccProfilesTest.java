package imagingbook.lib.color;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.lib.util.ResourceLocation;

public class IccProfilesTest {
	
	private ResourceLocation loc = new imagingbook.lib.color.DATA.iccProfiles.Resources();

	@Test
	public void test() {
		assertNotNull(loc.getResourcePath("AdobeRGB1998.icc"));
		assertEquals("wrong resource count", 7, loc.getResourceNames().length);
	}

}
