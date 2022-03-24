package imagingbook.lib.ij;

import java.util.List;

import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import imagingbook.lib.util.ClassUtils;

@Deprecated
public class PluginUtils {

	public static String[] listPlugins(String pkgName) {
		List<Class<?>> classes = ClassUtils.getClassesInPackage(pkgName);
		for (Class<?> clazz : classes) {
			System.out.println(clazz.getCanonicalName());
			System.out.println("is plugin = " + PlugIn.class.isAssignableFrom(clazz));
			System.out.println("is plugin = " + PlugInFilter.class.isAssignableFrom(clazz));
		}
		return null;
	}

	public static void main(String[] args) {
		Package pkg = Package.getPackage("imagingbook.lib.ij.overlay");
		System.out.println(pkg);
		pkg = imagingbook.lib.ij.overlay.ColoredStroke.class.getPackage();
		System.out.println(pkg);
		listPlugins("imagingbook.lib.ij.overlay");
		
//		for (Class<?> clazz: ClassUtils.getClassesInPackage("imagingbook.lib.ij.overlay")) {
//			System.out.println(clazz.getCanonicalName() + "  package = " + clazz.getPackage().getName());
//			Package p = clazz.getPackage();
//			
//			String name = p.getName();
//			Package p1 = Package.getPackage(name);
//			System.out.println(p1.getClass());
//			
//
//		}


	}

}
