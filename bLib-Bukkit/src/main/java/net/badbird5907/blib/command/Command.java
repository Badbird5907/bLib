package net.badbird5907.blib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command Framework - Command <br>
 * The command annotation used to designate methods as commands. All methods
 * should have a single CommandArgs argument
 *
 * @author minnymin3/Badbird5907/OctoPvP dev team
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	/**
	 * The name of the command. If it is a sub command then its values would be
	 * separated by periods. i.e. a command that would be a subcommand of test
	 * would be 'test.subcommandname'
	 *
	 * @return command name
	 */
	String name();

	/**
	 * Gets the required permission of the command
	 *
	 * @return command permission required
	 */
	String permission() default "";

	/**
	 * A list of alternate names that the command is executed under. See
	 * name() for details on how names work
	 *
	 * @return aliases
	 */
	String[] aliases() default {};

	/**
	 * The description that will appear in /help of the command
	 *
	 * @return description
	 */
	String description() default "";

	/**
	 * The usage that will appear in /help (commandname)
	 *
	 * @return usage
	 */
	String usage() default "";

	/**
	 * Whether the command is available to players only
	 *
	 * @return player only
	 */
	boolean playerOnly() default false;

	/**
	 * The cooldown in seconds
	 *
	 * @return cooldown
	 */
	int cooldown() default 0;

	/**
	 * wether to disable this command or not
	 *
	 * @return disable
	 */
	boolean disable() default false;
}
