package imagingbook.lib.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Any resource directory is supposed to contain a class extending
 * this class (the class name is arbitrary). 
 * This is to make sure that every resource directory
 * is also a Java package known to exist at compile time.
 * For example, in file {@code imagingbook.data.images.Resources.java}:
 * <pre>
 * package imagingbook.data.images;
 * public class Resources extends ResourceLocation { }
 * </pre>
 * All resources are assumed to be local in the SAME directory ONLY,
 * thereby avoiding the use of strings to specify sub-directories.
 * Example how to access the associated resources from some other class:
 * <pre>
 * ResourceLocation rd = new imagingbook.data.images.Resources();
 * Path path = rd.getResourcePath("boats.tif");
 * </pre>
 * Specifically, an image can be opened as follows:
 * <pre>
 * Path path = rd.getResourcePath("boats.tif");
 * ImagePlus im = IjUtils.openImage(path);
 * ImageProcessor ip = im.getProcessor();
 * ...
 * </pre>
 * Note that under the canonical Maven project structure, the associated file 
 * locations (package structure) are:
 * <pre>
 * src/main/java/imagingbook/data/images/Resources.java (the marker class extending {@link ResourceLocation})
 * src/main/resources/imagingbook/data/images/boats.tif ... (the actual resource files)
 * </pre>
 * or (if resources are used for testing only)
 * <pre>
 * src/test/java/imagingbook/data/images/Resources.java (the marker class extending {@link ResourceLocation})
 * src/test/resources/imagingbook/data/images/boats.tif ... (the actual resource files)
 * </pre>
 * 
 * @author WB
 * @version 2020/11/22
 */
public abstract class ResourceLocation {
	
	private final Class<? extends ResourceLocation> clazz;
	
	protected ResourceLocation() {
		this.clazz = this.getClass();
	}

	/**
	 * Determines if this class was loaded from
	 * a JAR file or from the file system.
	 * 
	 * @return true if contained in a JAR file, false otherwise
	 */
	public boolean isInsideJAR() {
		URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath();
		File file = new File(path);
		return file.isFile();
	}
	
	/**
	 * Returns the path to the file location of this class.
	 * @return The path to this class.
	 */
	public Path getResourcePath() {
		return getResourcePath("");
	}
	
	/**
	 * Returns the path to a resource relative to the location of this class.
	 *  
	 * @param resourceName The resource's simple name (including file extension)
	 * @return The path to the specified resource or {@code null} if not found
	 */
	public Path getResourcePath(String resourceName) {
		URI uri = getURI(resourceName);
		return (uri == null) ? null : uriToPath(uri);
	}
	
	/**
	 * Returns the specified resource as an {@link InputStream}.
	 * This is essentially a wrapper to {@link Class#getResourceAsStream(String)}.
	 * 
	 * @param resourceName The resource's simple name (including file extension)
	 * @return A stream or {@code null} if the resource is not found.
	 */
	public InputStream getResourceAsStream(String resourceName) {
		return clazz.getResourceAsStream(resourceName);
	}
	
	/**
	 * Returns the simple names of all resources within this class's directory
	 * (excluding the class file itself).
	 * This should work in an ordinary file system as well as a (possibly nested) 
	 * JAR file.
	 * 
	 * @return A sorted array of names (possibly empty)
	 */
	public String[] getResourceNames() {
		final String className = clazz.getSimpleName();
		Path[] paths = getResourcePaths(getResourcePath(""));
		List<String> nameList = new ArrayList<>(paths.length);
		for (Path p : paths) {
			String name = p.getFileName().toString();
			// exclude .java and .class files of THIS class
			String nameOnly = FileUtils.stripFileExtension(name);
			if (!nameOnly.equals(className)) {
				nameList.add(name);
			}
		}
		String[] sa = nameList.toArray(new String[0]);
		Arrays.sort(sa);
		return sa;
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * Finds the URI for a resource relative to (in the directory/package of) this class.
	 * The resource may be located in the file system or inside a JAR file.
	 * 
	 * @param resourceName The resource's simple name (including file extension)
	 * @return The URI or {@code null} if the resource was not found
	 */
	private URI getURI(String resourceName) {
		URI uri = null;
		if (isInsideJAR()) {
			String classPath = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
			//String packagePath = clazz.getPackage().getName().replace('.', File.separatorChar);
			String packagePath = clazz.getPackage().getName().replace('.', '/');
			String compPath = "jar:file:" + classPath + "!/" + packagePath + "/" + resourceName;
			try {
				uri = new URI(compPath);
			} catch (URISyntaxException e) {
				// throw new RuntimeException("getResourceURI: " + e.toString());
			}	
		}
		else {	// regular file path
			try {
				URL url = clazz.getResource(resourceName);
				if (url != null) {
					uri = url.toURI();
				}
			} catch (URISyntaxException e) {
				//do nothing, just return null
			}
		}
		return uri;
	}
	
	/**
	 * Converts an URI to a Path for locations that are either
	 * in the file system or inside a JAR file.
	 * 
	 * @param uri The specified location (URI)
	 * @return The associated path
	 */
	private static Path uriToPath(URI uri) {
		Path path = null;
		String scheme = uri.getScheme();
		switch (scheme) {
		case "jar":	{	// resource inside JAR file
			FileSystem fs = null;
			try { // check if this FileSystem already exists 
				fs = FileSystems.getFileSystem(uri);
			} catch (FileSystemNotFoundException e) {
				// that's OK to happen, the file system is not created automatically
			}
			
			if (fs == null) {	// must not create the file system twice
				try {
					fs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
				} catch (IOException e) {
					throw new RuntimeException("uriToPath: " + e.toString());
				}
			}
			
			String ssp = uri.getSchemeSpecificPart();
			int startIdx = ssp.lastIndexOf('!');
			String inJarPath = ssp.substring(startIdx + 1);  // in-Jar path (after the last '!')
			path = fs.getPath(inJarPath);
			break;
		}
		case "file": {	// resource in ordinary file system
			path = Paths.get(uri);
			break;
		}
		default:
			throw new IllegalArgumentException("Cannot handle this URI type: " + scheme);
		}
		return path;
	}
	
	/**
	 * Returns the paths to all files in a directory specified
	 * by a path. This should work in an ordinary file system
	 * as well as a (possibly nested) JAR file.
	 * 
	 * @param path The path to a directory (may be contained in a JAR file) 
	 * @return An array of paths or {@code null} if the specified path 
	 * is not a directory
	 */
	private static Path[] getResourcePaths(Path path) {
		// with help from http://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file, #10
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("path is not a directory: " + path.toString());
		}
		
		List<Path> pathList = new ArrayList<Path>();
		Stream<Path> walk = null;
		try {
			walk = Files.walk(path, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Iterator<Path> it = walk.iterator(); it.hasNext();){
			Path p = it.next();
			if (Files.isRegularFile(p) && Files.isReadable(p)) {
				pathList.add(p);
			}
		}
		walk.close();
		return pathList.toArray(new Path[0]);
	}
	
}
