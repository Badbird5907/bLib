package net.badbird5907.blib.util;

import lombok.Getter;
import lombok.Setter;

import static net.badbird5907.blib.util.CC.translate;
import static net.badbird5907.blib.utils.StringUtils.replacePlaceholders;

@Getter
@Setter
public class Logger {
	private static Logger instance;
	private static String prefix = "[bLib]";
	private static String debugPrefix = "[DEBUG]";
	private java.util.logging.Logger actualLogger;

	public Logger(java.util.logging.Logger logger, String prefix1, String dbg) {
		this.actualLogger = logger;
		prefix = prefix1;
		debugPrefix = dbg;
		instance = this;
	}

	public static void info(Object str, Object... placeholders) {
		instance.actualLogger.info(translate(replacePlaceholders(prefix + " " + str, placeholders)));
	}

	public static void warn(Object str, Object... placeholders) {
		instance.actualLogger.warning(translate(replacePlaceholders(prefix + " " + str, placeholders)));
	}

	public static void error(Object str, Object... placeholders) {
		instance.actualLogger.severe(translate(replacePlaceholders(prefix + " " + str, placeholders)));
	}

	public static void severe(Object str, Object... placeholders) {
		error(str, placeholders);
	}

	public static void debug(Object str, Object... placeholders) {
		info(debugPrefix + " " + replacePlaceholders(str.toString(), placeholders));
	}
}
