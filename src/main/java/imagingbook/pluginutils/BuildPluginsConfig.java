package imagingbook.pluginutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import imagingbook.lib.util.ClassUtils;
import imagingbook.lib.util.FileUtils;
import imagingbook.pluginutils.annotations.IjMenuEntry;
import imagingbook.pluginutils.annotations.IjMenuPath;
import imagingbook.pluginutils.annotations.PluginPackageName;

/**
 * The main() method of this class creates the 'plugins.config' file 
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
 * Example:
 * </p>
 * <pre>
 * import ij.plugin.filter.PlugInFilter;
 * import imagingbook.pluginutils.annotations.IjMenuEntry;
 * import imagingbook.pluginutils.annotations.IjMenuPath;
 * ...
 * @IjMenuPath("Plugins>Mine")
 * @IjMenuEntry("Super Plugin")
 * public class MySuperPlugin implements PlugInFilter {
 * 	// plugin code ...
 * }</pre>
 * <p>
 * Plugin classes (implementing {@link PlugIn} or {@link PlugInFilter}) may be annotated
 * with {@link IjMenuPath} and {@link IjMenuEntry} to specify where in ImageJ's menu tree
 * and by which name the plugin should be installed.
 * This information is stored in the 'plugins.config' file at the root of the associated project,
 * which is automatically added to the project's output JAR file during the Maven build.
 * By default (i.e., if no annotations are present), plugins are installed at the
 * top-level of 'Plugins' with their class name.
 * This also works for plugins in the project's 'default package', though this should be avoided.
 * </p>
 * <p>
 * Note that the information in 'plugins.config' is only used when plugins are loaded from 
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
		
		List<Class<?>> pluginClasses = getPluginClasses(rootPath);
		System.out.println("found plugins: " + pluginClasses.size());
		processPlugins(pluginClasses);
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
	}
	
	static void processPlugins(List<Class<?>> pluginClasses) {
		for (Class<?> clazz : pluginClasses) {
//			Package pkg = clazz.getPackage();
			
//			if (pkg == null) {
//				System.out.println("    "  + clazz.getSimpleName() + " / " + "Default Package");
//			}
//			else {
//				PluginPackageName ppn = pkg.getDeclaredAnnotation(PluginPackageName.class);
//				String annotation = (ppn == null) ? "no annotation" : ppn.value();
//				System.out.println("    "  + clazz.getSimpleName() + " / " + pkg.getName() + "(" + annotation + ")");
//			}
			
			IjMenuPath mp = clazz.getDeclaredAnnotation(IjMenuPath.class);
			IjMenuEntry me = clazz.getDeclaredAnnotation(IjMenuEntry.class);
			
			String menuPath = (mp != null) ? mp.value() : DefaultMenuPath ;
			String menuEntry = (me != null) ? me.value() : clazz.getSimpleName();
			
			System.out.format("%s, \"%s\", %s\n", menuPath, menuEntry, clazz.getCanonicalName());
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
