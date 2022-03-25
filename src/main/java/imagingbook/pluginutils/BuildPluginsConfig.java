package imagingbook.pluginutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import imagingbook.lib.util.ClassUtils;
import imagingbook.lib.util.FileUtils;
import imagingbook.pluginutils.annotations.IjMenuEntry;
import imagingbook.pluginutils.annotations.IjMenuPath;

/**
 * The {@code main()} method of this class creates the 'plugins.config' file 
 * for a plugins project, which is included in the associated JAR file.
 * The execution is to be triggered during the Maven build or manually by
 * <pre>
 * mvn exec:java -Dexec.mainClass="imagingbook.pluginutils.BuildPluginsConfig"</pre>
 * (at the root of a plugins project).
 * <p>
 * The format of the entries in 'plugins.config' have the following structure:
 * </p>
 * <pre>
 * Plugins, "Plugin Name", package.classname
 * Plugins&gt;Level1&gt;Level2, "Plugin Name", package.classname</pre>
 * <p>Technically, menu paths may have more than 2 levels, but this does not seem useful.
 * </p>
 * <p>
 * Plugin classes (implementing {@link PlugIn} or {@link PlugInFilter}) may be annotated
 * with {@link IjMenuPath} and {@link IjMenuEntry} to specify where in ImageJ's menu tree
 * and by which name the plugin should be installed.
 * This information is stored in the 'plugins.config' file at the root of the associated project,
 * which is automatically added to the project's output JAR file during the Maven build.
 * Example:
 * </p>
 * <pre>
 * import ij.plugin.filter.PlugInFilter;
 * import imagingbook.pluginutils.annotations.IjMenuEntry;
 * import imagingbook.pluginutils.annotations.IjMenuPath;
 * ...
 * @IjMenuPath("Plugins&gt;Mine")
 * @IjMenuEntry("Super Plugin")
 * public class MySuperPlugin implements PlugInFilter {
 * 	// plugin code ...
 * }</pre>
 * <p>
 * By default (i.e., if no annotations are present), plugins are installed at the
 * top-level of 'Plugins' with their class name.
 * This also works for plugins in the project's 'default package', though this should be avoided.
 * </p>
 * <p>
 * A {@link IjMenuPath} annotation may also be attached to a whole package
 * in the associated {@code package-info.java} file. The following
 * example specifies {@code Plugins>Binary Regions} as the default menu path
 * for all plugins in package {@code Binary_Regions}:
 * </p>
 * <pre>
 * @IjMenuPath("Plugins&gt;Binary Regions")
 * package Binary_Regions;
 * import imagingbook.pluginutils.annotations.IjMenuPath;</pre>
 * <p>
 * Individual plugins may override the menu path specified for the package.
 * <p>
 * Note that, in general, the information in 'plugins.config' is only used when plugins are loaded from 
 * a JAR file!
 * </p>
 * 
 * 
 * 
 * @author WB
 *
 */
public class BuildPluginsConfig {
	
	static String DefaultMenuPath = "Plugins";
	static String ConfigFileName = "plugins.config";
	
	static String artefactId;	// should be non-static
	
	/**
	 * Argument 1: project build root directory 
	 * Argument 2: project artefact id 
	 * @param args arguments
	 */
	public static void main(String[] args) {
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Running " + BuildPluginsConfig.class.getCanonicalName());
		System.out.println("args = ");
		for (String arg : args) {
			System.out.println("    " + arg);
		}
		
//		System.out.println("class loader = " + BuildPluginsConfig.class.getClassLoader().getResource(""));
		
		File rootDir = (args.length > 0) ?
				new File(args[0]) :
				new File(BuildPluginsConfig.class.getClassLoader().getResource("").getFile());
				
		String rootPath = rootDir.getAbsolutePath();
		System.out.println("path = " + rootPath);
		
		artefactId = (args.length > 1) ? args[1] : "UNKNOWN";
		
		List<Class<?>> pluginClasses = getPluginClasses(rootPath);
		System.out.println("found plugins: " + pluginClasses.size());
		processPlugins(pluginClasses, System.out);
		
		File configFile = new File(rootPath + "/" + ConfigFileName);
		System.out.println("configPath = " + configFile.getAbsolutePath());
		
		if (pluginClasses.isEmpty()) {
			System.out.println("Warning: no plugin classes found!");
		}
		else {
			try (PrintStream ps = new PrintStream(configFile)) {
				processPlugins(pluginClasses, ps);
				System.out.println("config file written and closed");
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
	}
	
//	void printToStream(PrintStream strm) {
	
	static void processPlugins(List<Class<?>> pluginClasses, PrintStream strm) {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		strm.println("# plugins.config file (automatically generated)");
//		strm.println("# imagingbook version: " + Info.getVersionInfo());
		strm.println("# project: " + artefactId);
		strm.println("# " + LocalDateTime.now().format(formatter));
		
		for (Class<?> clazz : pluginClasses) {
			Package pkg = clazz.getPackage();
			IjMenuPath packageMenuPath = null;
			
			if (pkg != null) {
				// see if 'package-info.java' contains specifies a menu path for this package
				packageMenuPath = pkg.getDeclaredAnnotation(IjMenuPath.class);
			}
			
			// see if clazz specifies a menu path for this package (overrules package specification)
			IjMenuPath classMenuPath = clazz.getDeclaredAnnotation(IjMenuPath.class);
			if (classMenuPath == null && packageMenuPath != null) {
				classMenuPath = packageMenuPath;
			}
			
			IjMenuEntry classMenuEntry = clazz.getDeclaredAnnotation(IjMenuEntry.class);
			
			String menuPath = (classMenuPath != null) ? classMenuPath.value() : DefaultMenuPath ;
			String menuEntry = (classMenuEntry != null) ? classMenuEntry.value() : clazz.getSimpleName();
			
			strm.format("%s, \"%s\", %s\n", menuPath, menuEntry, clazz.getCanonicalName());
		}
	}
	
	static List<Class<?>> getPluginClasses(String rootPath) {
		int n = rootPath.length();
		Path start = new File(rootPath).toPath();
		
		List<Class<?>> pluginClasses = new ArrayList<>();
		
		try (final Stream<Path> allPaths = Files.walk(start)) {
			allPaths.filter(Files::isRegularFile).forEach(path -> {
				String pathName = path.toString();
				//System.out.println("pathName = " + pathName);
				if (FileUtils.getFileExtension(pathName).equals("class")) {
					String className = FileUtils.stripFileExtension(pathName);
					className = className.substring(n + 1);
					className = className.replace(File.separatorChar, '.');
//					className = className.substring(className.indexOf(pkgName));
					System.out.println("     className = " + className);
					
					Class<?> clazz = null;
					try {
						clazz = Class.forName(className);
					} catch (final ClassNotFoundException ignored) {}
					
					if (clazz != null && ClassUtils.isIjPlugin(clazz)) {
						pluginClasses.add(clazz);
					}
					
					System.out.println("     class = " + clazz);
				}
			});
		} catch (IOException e) {
			//throw new RuntimeException(e.getMessage());
			System.out.println("SOMETHING HAPPENED: " + e.getMessage());
		}
		return pluginClasses;
	}

	@Deprecated
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
	
	/**
	 * Returns true if the specified {@link Class} object is a sub-type of
	 * {@link PlugIn} or {@link PlugInFilter}.
	 * 
	 * @param clazz a {@link Class} object
	 * @return true if a plugin type
	 */
	public static boolean isIjPlugin(Class<?> clazz) {
		return PlugIn.class.isAssignableFrom(clazz) || PlugInFilter.class.isAssignableFrom(clazz);
	}

}
