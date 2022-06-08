package net.badbird5907.blib.annotation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

//@Target(ElementType.)
@Retention(RUNTIME)
/**
 * Disables a command
 */
public @interface Disable {
	String reason() default "";
}
