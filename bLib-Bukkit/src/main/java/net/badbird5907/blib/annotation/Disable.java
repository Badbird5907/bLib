package net.badbird5907.blib.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Target(ElementType.)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Disables a command
 */
public @interface Disable {
	String reason() default "";
}
