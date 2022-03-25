package imagingbook.pluginutils.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used for specifying a menu name for plugins package.
 * @author WB
 *
 */
@Retention(RUNTIME)
@Target({ TYPE, PACKAGE })
public @interface PluginPackageName {
	public String value();
}
