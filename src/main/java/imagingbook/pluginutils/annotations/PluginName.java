package imagingbook.pluginutils.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotation to specify a the menu location where
 * the associated plugin should be installed, e.g.,
 * @PluginName("Plugins>Colorimetric_Stuff")
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface PluginName {
	public String value();
}
