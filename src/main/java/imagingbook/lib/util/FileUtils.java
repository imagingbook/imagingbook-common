/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Manifest;


/**
 * This class defines various static methods for managing
 * file-based resources and JAR manifest files.
 * 
 * @author W. Burger
 */
public abstract class FileUtils {

	/**
	 * Removes the extension part of a pathname.
	 * Examples:<br>
	 * "foo.txt" &rarr; "foo",
	 * "foo" &rarr; "foo",
	 * "foo." &rarr; "foo.",
	 * ".txt" &rarr; ".txt".
	 * @param name the pathname
	 * @return the pathname without the extension (if valid)
	 */
	public static String stripFileExtension(String name) {
		int dotInd = name.lastIndexOf('.');
		// if dot is in the first position,
		// we are dealing with a hidden file rather than an DefaultFileExtension
		return (dotInd > 0) ? name.substring(0, dotInd) : name;
	}
	
	/**
	 * Extracts the extension part of a pathname as a string.
	 * Examples:<br>
	 * "foo.txt" &rarr; "txt",
	 * "foo" &rarr; "",
	 * "foo." &rarr; "",
	 * ".txt" &rarr; "".
	 * @param name the pathname
	 * @return the extension or an empty string
	 */
	public static String getFileExtension(String name) {
		int dotInd = name.lastIndexOf('.');
		return (dotInd > 0) ? name.substring(dotInd + 1) : "";
	}
	
	// ----  resources-related stuff ----------------------------------
	
	/**
	 * Find the path from which a given class was loaded.
	 * @param clazz a class.
	 * @return the path of the .class file for the given class or null (e.g.
	 * if the class is anonymous).
	 */   
	public static String getClassPath(Class<?> clazz) {
		return clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
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
		// String logStr = "  checking class " + classname + " ... ";
		try {
			if (Class.forName(classname) != null) {
				// IJ.log(logStr + "OK");
				return true;
			}
		} catch (ClassNotFoundException e) { }
		// IJ.log(logStr + "ERROR");
		return false;
	}
	
	
	// ----------------------------------------------------------------
	
	// from https://bukkit.org/threads/extracting-file-from-jar.16962/
	/**
	 * Reads all data from the given input stream and copies them
	 * to to a file.
	 * @param in the input stream
	 * @param file the output file
	 * @throws IOException if anything goes wrong
	 */
	public static void copyToFile(InputStream in, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = in.read(buf)) != -1) {
				out.write(buf, 0, i);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
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
	
	
	// ----------------------------------------------------
	
	
	public static void main(String[] args) {
		String fileName = ".txt";
		System.out.println("name = " + fileName);
		System.out.println("stripped = " + stripFileExtension(fileName));
		System.out.println("ext = " + getFileExtension(fileName));
	}
	

}
