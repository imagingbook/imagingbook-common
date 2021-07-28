package imagingbook.lib.util.resource;

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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import ij.IJ;
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
 * ResourceLocation loc = new imagingbook.DATA.images.Resources();
 * Resource res = loc.getResource("boats.png");
 * URI uri = res.getUri();
 * InputStream strm = res.getStream();
 * ... * </pre>
 * 
 * Specifically, an image can be opened as follows:
 * <pre>
 * Resource res = loc.getResource("boats.png");
 * ImagePlus im = res.openAsImage();
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
 * @version 2021/07/28
 */
public abstract class ResourceLocation {
	
	private final boolean injar;
	private final URI baseURI;
	
	protected ResourceLocation() {
		this.injar = classInsideJar(this.getClass());
		this.baseURI = this.getResourceUri("");
	}
	
	private static boolean classInsideJar(Class<?> clazz) {
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
	
	public URI getUri() {
		return baseURI;
	}

	public Path getPath() {
		return toPath(baseURI);
	}
	
	/**
	 * Finds the {@link URI} for the the specified resource.
	 * The resource may be located in the file system or inside a JAR file.
	 * To convert the result to a {@link URL} use {@link URI#toURL()}.
	 * 
	 * @param resourceName The resource's simple name (including file extension, e.g., {@code "myimage.tif"}).
	 * @return The resource's {@link URI} or {@code null} if the resource was not found
	 */
	private URI getResourceUri(String resourceName) {
		Objects.requireNonNull(resourceName);
		URI uri = null;
		if (injar) {
			String classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
			//String packagePath = clazz.getPackage().getName().replace('.', File.separatorChar);
			String packagePath = this.getClass().getPackage().getName().replace('.', '/');
			String compPath = "jar:file:" + classPath + "!/" + packagePath + "/" + resourceName;
			try {
				uri = new URI(compPath);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e.toString());
			}	
		}
		else {	// regular file path
			try {
				URL url = this.getClass().getResource(resourceName);
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
	 * Returns an array of all resource names in this class's immediate directory
	 * (excluding this class's .java/.class files).
	 * 
	 * @return a sorted array of names (possibly empty)
	 */
	public String[] getResourceNames() {
		Path path = this.getPath();
		// with help from http://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file, #10
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("path is not a directory: " + path.toString());
		}
		
		List<String> nameList = new ArrayList<>();
		try (Stream<Path> walk = Files.walk(path, 1)) {		// walk is auto-closed!
			final String className = this.getClass().getSimpleName();
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path p = it.next();
				if (Files.isRegularFile(p) && Files.isReadable(p)) {	
					String name = p.getFileName().toString();
					// exclude .java and .class files of THIS class
					String nameOnly = FileUtils.stripFileExtension(name);
					if (!nameOnly.equals(className)) {
						nameList.add(name);
					}
				}
			}
		} catch (IOException e) { }

		String[] sa = nameList.toArray(new String[0]);
		Arrays.sort(sa);
		return sa;
	}
	
	// -------------------------------------------------------------------------------------
	
	/**
	 * Converts an {@link URI} to a {@link Path} for objects that are either
	 * in the file system or inside a JAR file.
	 * 
	 * @param uri the specified {@link URI}
	 * @return the associated {@link Path}
	 */
	public static Path toPath(URI uri) {
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
					Map<String, Object> map = Collections.emptyMap();
					fs = FileSystems.newFileSystem(uri, map);	// FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
				} catch (IOException e) {
					throw new RuntimeException(e.toString());
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
	
//	private HashMap<String, Resource> collectResources() {
//		final String className = this.getClass().getSimpleName();
//		//Path[] paths = getResourcePaths(this.path);
//		//Path[] paths = getResourcePaths(this.getPath());
//		Path[] paths = getResourcePaths();
//		HashMap<String, Resource> resTable = new HashMap<>();
//		for (Path p : paths) {
//			String n = p.getFileName().toString();
//			// exclude .java and .class files of THIS class
//			String nameOnly = FileUtils.stripFileExtension(n);
//			if (!nameOnly.equals(className)) {
//				resTable.put(n, new Resource(n, p));
//			}
//		}
//		return resTable;
//	}
	
	// --- these are the only methods exposed: -----
	
	public Resource getResource(String resourceName) {
		URI uri = getResourceUri(resourceName);
		return (uri == null) ? null : new Resource(resourceName, uri);
	}


//	public Resource[] getResources() {
//		return this.resources.toArray(new Resource[0]);
//		return this.resourceMap.values().toArray(new Resource[0]);
//	}
	
	// -------------------------------------------------------------------------------------

	/**
	 * Instances of this class represent individual resources contained in
	 * a particular {@link ResourceLocation}.
	 * This is a non-static class, since {@link Resource} instances can only
	 * exist in the context of a {@link ResourceLocation}.
	 */
	public class Resource {

		private final String name;
		private final URI uri;

		private Resource(String name, URI uri) {
			this.name = name;
			this.uri = toPath(uri).toUri();	// trick to get absolute URIs like "jar:file:///D:/..."
		}

		/**
		 * Returns the name of this resource.
		 * @return the name of this resource
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the {@link Path} to the associated resource object.
		 * @return the resource's {@link Path} 
		 */
		public Path getPath() {
			return toPath(uri);
		}

		/**
		 * Returns the {@link URI} for the associated resource object.
		 * @return the resource's {@link URI} 
		 */
		public URI getUri() {
			return uri;
		}

		public ImagePlus openAsImage() {
			return new Opener().openImage(uri.toString());
		}
		
		/**
		 * Returns an {@link InputStream} for reading from this resource.
		 * See also {@link Class#getResourceAsStream(String)}.
		 * @return an {@link InputStream} for this resource
		 */
		public InputStream getStream() {
			Class<?> clazz = ResourceLocation.this.getClass();
			IJ.log("clazz = " + clazz.getName());
			return clazz.getResourceAsStream(name);
		}

	}
}
