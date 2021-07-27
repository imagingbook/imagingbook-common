package imagingbook.lib.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import ij.ImagePlus;
import ij.io.Opener;
import imagingbook.lib.util.FileUtils;

/**
 * Any resource directory is supposed to contain a "marker" class 
 * (any legal class name is OK) extending this class ({@link ResourceLocation}).
 * This is to make sure that every resource location
 * is also a Java package known to exist at compile time.
 * For example, a marker class is defined in file {@code imagingbook.DATA.images.Resources.java} 
 * as follows:
 * <pre>
 * package imagingbook.DATA.images;
 * public class Resources extends ResourceLocation { }</pre>
 * 
 * All resources are assumed to be local in the SAME directory ONLY,
 * thereby avoiding the use of strings to specify sub-directories.
 * Here is an example how to access the associated resources from some other class:
 * <pre>
 * ResourceLocation rd = new imagingbook.DATA.images.Resources();
 * Path path = rd.getResourcePath("boats.png");</pre>
 * 
 * Specifically, an image can be opened as follows:
 * <pre>
 * Path path = rd.getResourcePath("boats.png");
 * ImagePlus im = IjUtils.openImage(path);
 * ImageProcessor ip = im.getProcessor();
 * ...</pre>
 * 
 * Note that under the canonical Maven project structure, the associated file 
 * locations (package structure) are:
 * <pre>
 * src/main/java/imagingbook/DATA/images/Resources.java (the marker class extending {@link ResourceLocation})
 * src/main/resources/imagingbook/DATA/images/boats.png ... (the actual resource files)</pre>
 * 
 * or (if resources are used for testing only)
 * <pre>
 * src/test/java/imagingbook/DATA/images/Resources.java (the marker class extending {@link ResourceLocation})
 * src/test/resources/imagingbook/DATA/images/boats.png ... (the actual resource files)</pre>
 * 
 * <p>
 * Note: To avoid marker classes showing up in the JavaDoc documentation,
 * we use the following (internal) "convention":
 * </p>
 * <ul>
 * <li>All root resource packages/directories are named {@code DATA} (deliberately
 * upper-case to mark them as special packages, since Java package names are generally lower-case).</li>
 * <li>The {@code pom.xml} file contains the following entry in the configuration
 * of the {@code maven-javadoc-plugin}:<br>
 * {@code <excludePackageNames>*.DATA.*</excludePackageNames>}
 * </ul>
 * Of course these packages could be named differently and excluded individually.
 * 
 * 
 * @author WB
 * @version 2020/11/22
 */
public abstract class ResourceLocation {
	
	private final Class<? extends ResourceLocation> resourceLocationClass;
	private final boolean injar;
	private final URI uri;
//	private final Path path;
	private final HashMap<String, Resource> resourceMap;
	
	protected ResourceLocation() {
		this.resourceLocationClass = this.getClass();
		this.injar = insideJAR(resourceLocationClass);
		this.uri = this.getURI("");
//		this.path = uriToPath(this.uri);
//		this.resources = collectResources();
		this.resourceMap = collectResources();
	}
	
	private static boolean insideJAR(Class<?> clazz) {
		URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath();
		File file = new File(path);
		return file.isFile();
	}

	/**
	 * Determines if this class was loaded from
	 * a JAR file or from the file system.
	 * 
	 * @return true if contained in a JAR file, false otherwise
	 */
	public final boolean insideJAR() {
//		URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
//		return new File(url.getPath()).isFile();
		return injar;
	}
	
	public URI getURI() {
		// return getPath("");
		return this.uri;
	}

	public Path getPath() {
		// return getPath("");
		//return this.path;
		return uriToPath(this.uri);
	}
	
	/**
	 * Returns the path to specified resource in this resource location.
	 *  
	 * @param resourceName The resource's simple name (including the file extension).
	 * If empty or {@code null}, the path to the resource container location (directory)
	 * is returned.
	 * @return The path to the specified resource or {@code null} if not found
	 * @deprecated // will become private!
	 */
	public Path getPath(String resourceName) {
		Objects.requireNonNull(resourceName);
		URI uri = getURI(resourceName);
		return (uri == null) ? null : uriToPath(uri);
	}
	
	/**
	 * Finds the {@link URI} for the the specified resource.
	 * The resource may be located in the file system or inside a JAR file.
	 * To convert the result to a {@link URL} use {@link URI#toURL()}.
	 * 
	 * @param resourceName The resource's simple name (including file extension, e.g., {@code "myimage.tif"}).
	 * @return The resource's {@link URI} or {@code null} if the resource was not found
	 * @deprecated // will become private!
	 */
	public URI getURI(String resourceName) {
		Objects.requireNonNull(resourceName);
		URI uri = null;
		if (insideJAR()) {
			String classPath = resourceLocationClass.getProtectionDomain().getCodeSource().getLocation().getFile();
			//String packagePath = clazz.getPackage().getName().replace('.', File.separatorChar);
			String packagePath = resourceLocationClass.getPackage().getName().replace('.', '/');
			String compPath = "jar:file:" + classPath + "!/" + packagePath + "/" + resourceName;
			try {
				uri = new URI(compPath);
			} catch (URISyntaxException e) {
				// throw new RuntimeException("getResourceURI: " + e.toString());
			}	
		}
		else {	// regular file path
			try {
				URL url = resourceLocationClass.getResource(resourceName);
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
	 * Converts an {@link URI} to a {@link Path} for objects that are either
	 * in the file system or inside a JAR file.
	 * 
	 * @param uri the specified {@link URI}
	 * @return the associated {@link Path}
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
	 * Returns the specified resource as an {@link InputStream}.
	 * This is essentially a wrapper to {@link Class#getResourceAsStream(String)}.
	 * 
	 * @param resourceName The resource's simple name (including file extension)
	 * @return A stream or {@code null} if the resource is not found.
	 * @deprecated  // use method on Resource instance!
	 */
	public InputStream getResourceAsStream(String resourceName) {
		return resourceLocationClass.getResourceAsStream(resourceName);
	}
	
	/**
	 * Returns the simple names of all resources within this class's directory
	 * (excluding the class file itself).
	 * This should work in an ordinary file system as well as a (possibly nested) 
	 * JAR file.
	 * 
	 * @return A sorted array of names (possibly empty)
	 * @deprecated // use getResources()
	 */
	public String[] getResourceNames() {
		final String className = resourceLocationClass.getSimpleName();
		Path[] paths = getResourcePaths(getPath(""));
		//Path[] paths = getResourcePaths();
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
		try (Stream<Path> walk = Files.walk(path, 1)) {
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path p = it.next();
				if (Files.isRegularFile(p) && Files.isReadable(p)) {
					pathList.add(p);
				}
			}
			walk.close();
		} catch (IOException e) { }

		return pathList.toArray(new Path[0]);
	}
	
	public Path[] getResourcePaths() {
		return getResourcePaths(this.getPath()); 
		//return getResourcePaths(Paths.get(getURI()));  // why not working???
	}
	
	// -------------------------------------------------------------------------------------
	
	private HashMap<String, Resource> collectResources() {
		final String className = this.resourceLocationClass.getSimpleName();
		//Path[] paths = getResourcePaths(this.path);
		//Path[] paths = getResourcePaths(this.getPath());
		Path[] paths = getResourcePaths();
		HashMap<String, Resource> resTable = new HashMap<>();
		for (Path p : paths) {
			String n = p.getFileName().toString();
			// exclude .java and .class files of THIS class
			String nameOnly = FileUtils.stripFileExtension(n);
			if (!nameOnly.equals(className)) {
				resTable.put(n, new Resource(n, p));
			}
		}
		return resTable;
	}
	
	// --- these are the only methods exposed: -----
	
	public Resource getResource(String resourceName) {
//		Path p = getPath(resourceName);
//		return (p == null) ? null : new Resource(resourceName, p);
		return resourceMap.get(resourceName);
	}
	
	public Resource[] getResources() {
		//return this.resources.toArray(new Resource[0]);
		return this.resourceMap.values().toArray(new Resource[0]);
	}
	
	// -------------------------------------------------------------------------------------
	
	/**
	 * Instances of this class represent individual resources contained in
	 * a particular {@link ResourceLocation}.
	 * This is a non-static inner class, i.e., a {@link Resource} can only be instantiated
	 * (exist) within the context of a surrounding {@link ResourceLocation}.
	 */
	public class Resource {
		
		private final String name;
		private final Path path;
		public URL url = null;
		
		Resource(String name, Path path) {
			this.name = name;
			this.path = path;
		}
		
		public String getName() {
			return name;
		}
		
		public Path getPath() {
			return path;
		}
		
		// untested!
		public URL getURL() {
			URL url = null;
			try {
				url = path.toUri().toURL();
			} catch (MalformedURLException e) {	}
			return url;
		}
		
		public ImagePlus openAsImage() {
			URL url = getURL();
			return new Opener().openImage(url.toString());
		}
		
		/**
		 * Returns an {@link InputStream} for reading from this resource.
		 * See also {@link Class#getResourceAsStream(String)}.
		 * @return an {@link InputStream} for this resource
		 */
		public InputStream getStream() {
			return resourceLocationClass.getResourceAsStream(name);
		}

	}
	
}
