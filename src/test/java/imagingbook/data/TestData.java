package imagingbook.data;

import java.nio.file.Path;

import ij.ImagePlus;
import ij.io.Opener;
import imagingbook.lib.util.ResourceUtils;

/**
 * This class defines static method for simplified access to local
 * test resources, particularly images.
 * Note that this mechanism is available for unit 
 * testing only, it is not contained in the target JAR file!
 * 
 * This class also serves as the root class for the test data resource
 * tree. All resources are stored in sub-directories (i.e., sub-packages).
 * Images are located in {@code ./images/} by default, i.e., in
 * {@code src/test/resources/imagingbook/data/images/}.
 * 
 * @author WB
 * @version 2020/11/21
 */
public abstract class TestData {
	
	/**
	 * Locates the specified resource and returns the associated path
	 * or {@code null} if non-existent.
	 * 
	 * @param subdir The directory relative to this class ({@link TestData}), e.g., {@code "images"} or {@code "images/foo"}
	 * @param name The simple file name of the resource (including extension), e.g., {@code "bar.txt"}
	 * @return The path to the specified resource or null if not found
	 */
	public static Path getResourcePath(String subdir, String name) {
		if (!subdir.endsWith("/")) {
			subdir = subdir + "/";
		}
		return ResourceUtils.getResourcePath(TestData.class, subdir + name);
	}
	
	/**
	 * Locates the specified image and returns the associated path
	 * or {@code null} if non-existent.
	 * 
	 * @param name The simple file name of the image (including extension), e.g., {@code "boats.tif"}
	 * @return The path to the specified image or null if not found
	 */
	public static Path getImagePath(String name) {
		return getResourcePath("images/", name);
	}
	
	/**
	 * Opens the specified image resource and returns it
	 * as a {@link ImagePlus} instance.
	 * 
	 * @param name The simple name of the image file (including extension)
	 * @return A new {@link ImagePlus} instance or {@code null} if not found
	 */
	public static ImagePlus openImage(String name) {
		Path path = getImagePath(name);
		return (path == null) ? null :new Opener().openImage(path.toString()); 
	}
}
