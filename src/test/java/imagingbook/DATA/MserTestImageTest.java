package imagingbook.DATA;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import imagingbook.core.resource.ImageResource;


/**
 * Checks if images for all named resources exist.
 * @author WB
 *
 */
public class MserTestImageTest {

	@Test
	public void test1() {
	for (ImageResource ir : MserTestImage.values()) {
		assertNotNull("could not find URI for resource " + ir.toString(), ir.getURI());
		assertNotNull("could not open image for resource " + ir,  ir.getImage());
	}
}

}
