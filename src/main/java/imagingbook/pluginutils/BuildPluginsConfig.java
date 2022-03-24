package imagingbook.pluginutils;

import java.io.File;
import java.util.List;

import imagingbook.lib.util.ClassUtils;

/**
 * The main() method of this class creates the 'plugins.config' file 
 * for a plugins project, which is included in the associated JAR file.
 * The execution is to be triggered during the Maven build or manually by
 * {@code mvn exec:java -Dexec.mainClass="imagingbook.pluginutils.BuildPluginsConfig"}
 * (at the root of a plugins project).
 * 
 * @author WB
 *
 */
public class BuildPluginsConfig {
	
	public static void main(String[] args) {
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Running " + BuildPluginsConfig.class.getCanonicalName());
		System.out.println("args = ");
		for (String arg : args) {
			System.out.println("    " + arg);
		}
		System.out.println("path = " + new File("").getAbsolutePath());
		for (Class<?> clazz : ClassUtils.getClassesInPackage("")) {
			System.out.println(clazz);
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
	}
	
//	public static void main(String[] args) {
////		listPlugins("Colorimetric_Color");
////		listPlugins("");
//		for (Class<?> clazz : ClassUtils.getClassesInPackage("")) {
//			System.out.println(clazz);
//		}
//	}

	
	static String[] listPlugins(String pkgName) {
		System.out.println("package = " + pkgName);
		List<Class<?>> classes = ClassUtils.getClassesInPackage(pkgName);
		String pluginPkg = getDirectPackageName(pkgName);
		System.out.println("containing package = " + pluginPkg);
		for (Class<?> clazz : classes) {
//			System.out.println(clazz.getCanonicalName());
			if (ClassUtils.isIjPlugin(clazz)) {
				String className = clazz.getCanonicalName();
				String pluginName = clazz.getSimpleName();
				if (pluginPkg.isEmpty()) {
					System.out.format("Plugins, \"%s\", %s\n", pluginName, className);
				}
				else {
					System.out.format("Plugins>%s, \"%s\", %s\n", pluginPkg, pluginName, className);
				}
				
			}
		}
		return null;
	}
	
	static String getDirectPackageName(Package pkg) {
		return getDirectPackageName(pkg.getName());
	}
	
	static String getDirectPackageName(String name) {
		int dotIdx = name.lastIndexOf('.');
		return (dotIdx > 0) ? name.substring(dotIdx + 1) : name;
	}

}
