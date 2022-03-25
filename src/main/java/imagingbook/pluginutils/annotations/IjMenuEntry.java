package imagingbook.pluginutils.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotation to specify the menu entry (name) of the associated plugin, e.g.
 * @IjMenuEntry("Super Plugin!")
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface IjMenuEntry {
	public String value();
}
