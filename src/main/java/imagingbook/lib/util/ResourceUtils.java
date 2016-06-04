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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;

/**
 * This class defines static methods for accessing resources.
 *
 * @author W. Burger
 * @version 2016/06/04
 *
 */
public class ResourceUtils {
	
	/**
	 * Determines if the specified clazz was loaded from
	 * a JAR file or a .class file in the file system.
	 * 
	 * @param clazz the class
	 * @return true if contained in a JAR file
	 */
	public static boolean isInJar(Class<?> clazz) {
		URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath();
		File file = new File(path);
		return file.isFile();
	}
	
	/**
	 * Finds the URI for a resource relative to a specified class.
	 * The resource may be located in the file system or
	 * inside a JAR file.
	 * 
	 * @param clazz the anchor class
	 * @param resPath the resource path relative to the anchor class
	 * @return the URI or {@code null} if the resource was not found
	 */
	public static URI getResourceUri(Class<?> clazz, String  relPath) {
		URI uri = null;
		if (isInJar(clazz)) {
			String classPath = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
			String packagePath = clazz.getPackage().getName().replace('.', File.separatorChar);
			String compPath = "jar:file:" + classPath + "!/" + packagePath + "/" + relPath;
			try {
				uri = new URI(compPath);
			} catch (URISyntaxException e) {
				throw new RuntimeException("getResourceURI: " + e.toString());
			}	
		}
		else {
			System.out.println("regular file path");
			try {
				uri = clazz.getResource(relPath).toURI();
			} catch (Exception e) {
				throw new RuntimeException("getResourceURI: " + e.toString());
			}
		}
		return uri;
	}
	
	/**
	 * Find the path to a resource relative to the location of class c.
	 * Example: Assume class C was loaded from file someLocation/C.class
	 * and there is a subfolder someLocation/resources/ that contains 
	 * an image 'lenna.jpg'. Then the absolute path to this image
	 * is obtained by 
	 * String path = getResourcePath(C.class, "resources/lenna.jpg");
	 * 
	 * 2016-06-03: modified to return proper path to resource inside 
	 * a JAR file.
	 * 
	 * @param clazz anchor class 
	 * @param relPath the path of the resource to be found (relative to the location of the anchor class)
	 * @return the path to the specified resource
	 */
	public static Path getResourcePath(Class<?> clazz, String  relPath) {
		URI uri = getResourceUri(clazz, relPath);
		if (uri != null) {
			return uriToPath(uri);
		}
		else {
			return null;
		}
	}
	
	public static Path uriToPath(URI uri) {
		Path path = null;
		String scheme = uri.getScheme();
		switch (scheme) {
		case "file": {	// resource in ordinary file system
			path = Paths.get(uri);
			break;
		}
		case "jar":	{	// resource inside JAR file
			FileSystem fs = null;
			try { // check if this FileSystem already exists (can't create twice!)
				fs = FileSystems.getFileSystem(uri);
			} catch (FileSystemNotFoundException e) {
				// that's OK to happen, the file system is not created automatically
			}

			try {
				fs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
			} catch (IOException e) {
				throw new RuntimeException("uriToPath: " + e.toString());
			}
			
			String ssp = uri.getSchemeSpecificPart();
			int startIdx = ssp.lastIndexOf('!');
			String inJarPath = ssp.substring(startIdx + 1);  // in-Jar path (after the last '!')
			path = fs.getPath(inJarPath);
			break;
		}
		default:
			throw new IllegalArgumentException("Cannot handle this URI type: " + scheme);
		}
		return path;
	}
	
	
	public static Path[] listResources(URI uri) {
		return listResources(uriToPath(uri));
	}
	
	
	/**
	 * Method to obtain the paths to all files in a directory specified
	 * by a path. This should work in an ordinary file system
	 * as well as a (possibly nested) JAR file.
	 * 
	 * @param path path to a directory (may be contained in a JAR file) 
	 * @return a sequence of paths or {@code null} if the specified path is not a directory
	 */
	public static Path[] listResources(Path path) {
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
	
	/**
	 * Use this method to obtain the paths to all files in a directory located
	 * relative to the specified class. This should work in an ordinary file system
	 * as well as a (possibly nested) JAR file.
	 * 
	 * @param clazz class whose source location specifies the root 
	 * @param relPath path relative to the root
	 * @return a sequence of paths or {@code null} if the specified path is not a directory
	 */
	public static Path[] listResources(Class<?> clazz, String relPath) {
		return listResources(getResourceUri(clazz, relPath));
	}
	
	
	/**
	 * Opens an image from the specified resource. 
	 * If the resource is contained inside a JAR file, it is first
	 * extracted to a temporary file and subsequently opened
	 * with ImageJ's {@code Opener} class. 
	 * 
	 * @param clazz the anchor class
	 * @param resDir the directory relative to the anchor class
	 * @param resName the (file) name of the image resource
	 * @return the opened image or {@code null} if not successful.
	 */
	public static ImagePlus openImageFromResource(Class<?> clazz, String resDir, String resName) {
		URI uri = getResourceUri(clazz, resDir + resName);
		if (uri == null) {
			IJ.error("resource not found: " + clazz.getName() + " | " + resDir  + " | " + resName);
			return null;
		}
		
		ImagePlus im = null;
		
		String scheme = uri.getScheme();
		switch (scheme) {
		case "file": {	// resource in ordinary file system
			Path path = Paths.get(uri);
			im = new Opener().openImage(path.toString());
			break;
		}
		case "jar": { // resource inside JAR
			// create a temporary file:
			String ext = FileUtils.getFileExtension(resName);
			File tmpFile = null;
			try {
				tmpFile = File.createTempFile("img", "." + ext);
				tmpFile.deleteOnExit();
			} 
			catch (IOException e) {
				throw new RuntimeException("Could not create temporary file");
			}
						
			//IJ.log("copying to tmp file: " + tmpFile.getPath());
			String relPath = resDir + resName;
			InputStream inStrm = clazz.getResourceAsStream(relPath);
			
			try {
				FileUtils.copyToFile(inStrm, tmpFile);
			} catch (IOException e) {
				throw new RuntimeException("Could not copy stream to temporary file");
			}
			im = new Opener().openImage(tmpFile.getPath());
			if (im != null) {
				im.setTitle(resName);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Cannot handle this resource type: " + scheme);
		}
		return im;
	}
	
	// OLD from HERE ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * Finds the URI for a resource relative to a specified class.
	 * The resource may be located in the file system or
	 * onside a JAR file.
	 * 
	 * @param clazz the anchor class
	 * @param resDir the directory relative to the anchor class
	 * @param resName the (file) name of the resource
	 * @return the URI or {@code null} if the resource was not found
	 * @deprecated
	 */
//	public static URI getResourceURI(Class<?> clazz, String resDir, String resName) {
//		String relPath = resDir + resName;
//		URL url = clazz.getResource(relPath);
//		if (url == null) {
//			return null;
//		}
//		URI uri = null;
//		try {
//			uri = url.toURI();
//		} catch (URISyntaxException e) { }
//		return uri;
//	}

	/**
	 * Find the path to a resource relative to the location of class c.
	 * Example: Assume class C was loaded from file someLocation/C.class
	 * and there is a subfolder someLocation/resources/ that contains 
	 * an image 'lenna.jpg'. Then the absolute path to this image
	 * is obtained by 
	 * String path = getResourcePath(C.class, "resources/lenna.jpg");
	 * 
	 * 2016-06-03: modified to return proper path to resource inside 
	 * a JAR file.
	 * 
	 * @param clazz anchor class 
	 * @param relPath the path of the resource to be found (relative to the location of the anchor class)
	 * @return the path to the specified resource
	 */
//	public static Path getResourcePath(Class<?> clazz, String relPath) {
//		URI uri = null;
//		try {
//			uri = clazz.getResource(relPath).toURI();
//		} catch (Exception e) {
//			System.err.println(e);
//			return null;
//		}
//		
//		IJ.log("getResourcePath(): uri = " + uri.toString());
//		Path path = null;
//		//IJ.log("getResourcePath(): path = " + path);
//		String scheme = uri.getScheme();
//		
//		switch (scheme) {
//		case "file": {	// resource in ordinary file system
//			path = Paths.get(uri);
//			break;
//		}
//		case "jar":	{	// resource inside a JAR file
//			FileSystem fs = null;
//			try {
//				// check if this FileSystem already exists (can't create twice!)
//				fs = FileSystems.getFileSystem(uri);
//			} catch (Exception e) { }
//	
//			if (fs == null) {	// FileSystem does not yet exist in this runtime
//				try {
//					fs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
//				} catch (IOException e) { }
//			}
//			
//			if (fs == null) {	// FileSystem could not be created for some reason
//				throw new RuntimeException("FileSystem could not be created");
//			}
//			String ssp = uri.getSchemeSpecificPart();
//			int startIdx = ssp.lastIndexOf('!');
//			String inJarPath = ssp.substring(startIdx + 1);  // in-Jar path (after the last '!')
//			path = fs.getPath(inJarPath);
//			break;
//		}
//		default:
//			throw new IllegalArgumentException("Cannot handle path type: " + scheme);
//		}
//		return path;
//	}

//	/**
//	 * Checks 'by name' of a particular resource exists.
//	 * 
//	 * @param classname name of the class, e.g. {@literal imagingbook.lib.util.FileUtils}
//	 * @param recourcePath path (relative to the location of the class) to the specified resource 
//	 * @return {@code true} if the specified resource was found, {@code false} otherwise
//	 */
//	public static boolean checkResource(String classname, String recourcePath) {
//		String logStr = "  checking resource " + classname + ":" + recourcePath + " ... ";
//		try {
//			//if (Class.forName(classname).getResourceAsStream(recourcePath) != null) {
//			if (Class.forName(classname).getResource(recourcePath)!= null) {
//				IJ.log(logStr + "OK");
//				return true;
//			}
//		} catch (Exception e) {	}
//		IJ.log(logStr + "ERROR");
//		return false;
//	}

	
	
	/**
	 * Finds a resource relative to the location of class clazz and returns
	 * a stream to read from.
	 * Example: Assume class C was loaded from file someLocation/C.class
	 * and there is a subfolder someLocation/resources/ that contains 
	 * an image 'lenna.jpg'. Then the input stream for reading this this image
	 * is obtained by
	 * 
	 * InputStream strm = getResourceStream(C.class, "resources/lenna.jpg");
	 * 
	 * This should access resources stored in the file system or inside a JAR file!
	 * 
	 * @param clazz anchor class 
	 * @param relativePath Path to the resource to be found (relative to the location of {@literal clazz}.
	 * @return A stream for reading the resource or null if not found.
	 * @deprecated
	 */
//	public static InputStream getResourceStream(Class<?> clazz, String relativePath) {
//		return clazz.getResourceAsStream(relativePath);
//	}







	
//	public static ImagePlus openImageFromResource(Class<?> clazz, String resDir, String resName) {
//		URI uri = getResourceUri(clazz, resDir + resName);
//		if (uri == null) {
//			IJ.error("resource not found: " + clazz.getName() + " | " + resDir  + " | " + resName);
//			return null;
//		}
//		
//		String scheme = uri.getScheme();
//		switch (scheme) {
//		case "file": {	// resource in ordinary file system
//			Path path = Paths.get(uri);
//			//IJ.log("opening image from file");
//			return new Opener().openImage(path.toString());
//		}
//		case "jar": { // resource inside JAR
//			// create a temporary file:
//			String ext = FileUtils.getFileExtension(resName);
//			File tmpFile = null;
//			try {
//				tmpFile = File.createTempFile("img", "." + ext);
//				tmpFile.deleteOnExit();
//			} 
//			catch (IOException e) {
//				IJ.error("Could not create temporary file");
//				return null;
//			}
//						
//			//IJ.log("copying to tmp file: " + tmpFile.getPath());
//			String relPath = resDir + resName;
//			InputStream inStrm = clazz.getResourceAsStream(relPath);
//			ImagePlus im = null;
//			try {
//				FileUtils.copyToFile(inStrm, tmpFile);
//			} catch (IOException e) {
//				IJ.error("Could not copy stream to temporary file");
//				return null;
//			}
//			im = new Opener().openImage(tmpFile.getPath());
//			if (im != null) {
//				im.setTitle(resName);
//			}
//			return im;
//		}
//		default:
//			throw new IllegalArgumentException("Cannot handle this resource type: " + scheme);
//		}
//	}

}
