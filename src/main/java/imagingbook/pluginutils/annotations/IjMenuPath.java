package imagingbook.pluginutils.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to specify the menu entry (name) of the associated plugin
 * @IjMenuPath("Plugins>My Stuff")
 */
@Retention(RUNTIME)
@Target({ TYPE, PACKAGE })
public @interface IjMenuPath {
	public String value();
}
