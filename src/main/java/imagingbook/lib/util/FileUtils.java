/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package imagingbook.lib.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import ij.IJ;


public abstract class FileUtils {

	// from: http://www.java2s.com/Tutorial/Java/0180__File/StripFileExtension.htm
	public static String stripFileExtension(String fileName) {
		int dotInd = fileName.lastIndexOf('.');
		// if dot is in the first position,
		// we are dealing with a hidden file rather than an DefaultFileExtension
		return (dotInd > 0) ? fileName.substring(0, dotInd) : fileName;
	}

	/**
	 * Find the path from which a given class was loaded.
	 * @param clazz a class.
	 * @return the path of the .class file for the given class or null (e.g.
	 * if the class is anonymous).
	 */   
	public static String getClassPath(Class<?> clazz) {
		return getResourcePath(clazz, "");
	}

	/**
	 * Find the path to a resource relative to the location of class c.
	 * Example: Assume class C was loaded from file someLocation/C.class
	 * and there is a subfolder someLocation/resources/ that contains 
	 * an image 'lenna.jpg'. Then the absolute path to this image
	 * is obtained by 
	 * String path = getResourcePath(C.class, "resources/lenna.jpg");
	 * 
	 * @param clazz anchor class 
	 * @param name name of the resource to be found
	 * @return the path for the resource or null if not found.
	 */
	public static String getResourcePath(Class<?> clazz, String name) {
		URL url = clazz.getResource(name);
		if (url == null) {
			return null;
		}
		else {
			return url.getPath();
		}
	}
	
	// New: to allow reading from JAR files.
//	public static URI getResourceURI(Class<?> clazz, String name) {
//		URL url = clazz.getResource(name);
//		URI uri = null;
//		if (url != null) {
//			try {
//				uri = url.toURI();
//			} catch (URISyntaxException e) {}
//			
//		}
//		return uri;
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
	 */
	public static InputStream getResourceStream(Class<?> clazz, String relativePath) {
		return clazz.getResourceAsStream(relativePath);
	}
	
	// ----------------------------------------------------------------

	/**
	 * Lists (to System.out) the paths where classes are loaded from.
	 */
	public static void printClassPath() {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader) cl).getURLs();
		for (URL url : urls) {
			System.out.println(url.getPath());
		}

	}
	
	// ----------------------------------------------------------------
	
	/**
	 * Checks 'by name' if a particular class exists.
	 * 
	 * @param classname fully qualified name of the class, e.g. {@literal imagingbook.lib.util.FileUtils}
	 * @return {@code true} if the class was found, {@code false} otherwise
	 */
	public static boolean checkClass(String classname) {
		String logStr = "  checking class " + classname + " ... ";
		try {
			if (Class.forName(classname) != null) {
				IJ.log(logStr + "OK");
				return true;
			}
		} catch (ClassNotFoundException e) { }
		IJ.log(logStr + "ERROR");
		return false;
	}
	
	/**
	 * Checks 'by name' of a particular resource exists.
	 * 
	 * @param classname name of the class, e.g. {@literal imagingbook.lib.util.FileUtils}
	 * @param recourcePath path (relative to the location of the class) to the specified resource 
	 * @return {@code true} if the specified resource was found, {@code false} otherwise
	 */
	public static boolean checkResource(String classname, String recourcePath) {
		String logStr = "  checking resource " + classname + ":" + recourcePath + " ... ";
		try {
			//if (Class.forName(classname).getResourceAsStream(recourcePath) != null) {
			if (Class.forName(classname).getResource(recourcePath)!= null) {
				IJ.log(logStr + "OK");
				return true;
			}
		} catch (Exception e) {	}
		IJ.log(logStr + "ERROR");
		return false;
	}
	
	/**
	 * Use this method to obtain the paths to all files in a directory located
	 * relative to the specified class. This should work in an ordinary file system
	 * as well as a (possibly nested) JAR file.
	 * TODO: change to return empty array instead of null.
	 * 
	 * @param theClass class whose source location specifies the root 
	 * @param relPath path relative to the root
	 * @return a sequence of paths or {@code null} if the specified path is not a directory
	 */
	public static Path[] listResources(Class<?> theClass, String relPath) {
		URI uri = null;
		try {
			uri = theClass.getResource(relPath).toURI();
			//IJ.log("uri = " + uri.getPath().toString());
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
		Path resourcePath = null;
		String scheme = uri.getScheme();
		
		switch (scheme) {
		case "file": {	// resource in ordinary file system
			resourcePath = Paths.get(uri);
			break;
		}
		case "jar":	{	// resource inside a JAR file
			FileSystem fs = null;
			try {
				// wilbur: check if this FileSystem already exists (can't create twice!)
				fs = FileSystems.getFileSystem(uri);
			} catch (Exception e) {}

			if (fs == null) {	// FileSystem does not yet exist in this runtime
				try {
					fs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
				} catch (IOException e) { }
			}
			
			if (fs == null) {	// FileSystem could not be created for some reason
				return null;
			}
			String ssp = uri.getSchemeSpecificPart();
			int start = ssp.lastIndexOf('!');
			String inJarPath = ssp.substring(start + 1);  // remove leading part including the last '!'
			//System.out.println("[listResources] inJarPath = "  + inJarPath);
			resourcePath = fs.getPath(inJarPath);
			break;
		}
		default:
			throw new IllegalArgumentException("Cannot handle this path type: " + scheme);
		}
		
		if (!Files.isDirectory(resourcePath)) {
			return null; 	// cannot list if no directory
		}
		
		// Path 'resourcePath' is a directory

		List<Path> rlst = new ArrayList<Path>();
		// with help from http://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file, #10
		Stream<Path> walk;
		try {
			walk = Files.walk(resourcePath, 1);
		} catch (IOException e) {
			System.err.println(e);
			return null;
		}	    
		for (Iterator<Path> it = walk.iterator(); it.hasNext();){
			Path p = it.next();
			//	        try {
			//				System.out.println("[listResources] " + p.getFileName().toString() + " " + Files.isRegularFile(p));
			//			} catch (Exception e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			if (Files.isRegularFile(p) && Files.isReadable(p)) {
				rlst.add(p);
			}
		}
		walk.close();
		return rlst.toArray(new Path[0]);
	}
	
	
	// ----------------------------------------------------------------
	
	/**
	 * Finds the manifest (from META-INF/MANIFEST.MF) of the JAR file
	 * from which {@literal clazz} was loaded.
	 * 
	 * See: http://stackoverflow.com/a/1273432
	 * @param clazz A class in the JAR file of interest.
	 * @return A {@link Manifest} object or {@literal null} if {@literal clazz}
	 * was not loaded from a JAR file.
	 */
	public static Manifest getJarManifest(Class<?> clazz) {
		String className = clazz.getSimpleName() + ".class";		
		String classPath = clazz.getResource(className).toString();
		//IJ.log("classPath = " + classPath);
		if (!classPath.startsWith("jar")) { // Class not from JAR
		  return null;
		}
		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
		Manifest manifest = null;
		try {
			manifest = new Manifest(new URL(manifestPath).openStream());
		} catch (IOException ignore) { }
		return manifest;
	}
	
	
	// new stuff:


	

}
