/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import imagingbook.lib.util.FileUtils;

public abstract class Info {
	
	/**
	 * Reads version information from the MANIFEST.MF file of the JAR file from
	 * which this class was loaded. 
	 * 
	 * @return A string with the version information. 
	 * The string "UNKNOWN" is returned if the class was not loaded from a JAR file or if 
	 * the version information could not be determined.
	 */
	public static String getVersionInfo() {
		Manifest mf = FileUtils.getJarManifest(Info.class);
		if (mf == null) {
			return "UNKNOWN";
		}
		//IJ.log("listing attributes");
		Attributes attr = mf.getMainAttributes();
		String version = null;
		String buildDate = null;
		try {
			version = attr.getValue("Implementation-Version");
			buildDate = attr.getValue("Build-Date");
		} catch (IllegalArgumentException e) { }
		return version + " (" + buildDate + ")";
	}
	
	
	/**
	 * Defined {@literal public} to show in JavaDoc.
	 * Obsolete (reset to {@literal private} again). 
	 * @deprecated
	 */
	private static final int VERSION = 99999999;	
	
	/**
	 * This method is deprecated, version dates are not maintained any longer. Use
	 * the method {@link getVersionInfo} to retrieve the Maven build version instead.
	 * 
	 * @return The current version of the 'imagingbook' library as an 8-digit integer,
	 * eg 20130721 (in YYYYMMDD-format).
	 * @deprecated
	 */
	public static int getVersion() {
		return VERSION;
	}


}
