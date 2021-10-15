package net.badbird5907.blib.util;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.utils.StringUtils;

@Getter
@Setter
public class Logger {
    private java.util.logging.Logger actualLogger;
    private static Logger instance;
    private static String prefix = "[bLib]";
    private static String debugPrefix = "[DEBUG]";
    public Logger(java.util.logging.Logger logger, String prefix1,String dbg){
        this.actualLogger = logger;
        prefix = prefix1;
        debugPrefix = dbg;
        instance = this;
    }
    public static void info(Object str,Object... placeholders){
        instance.actualLogger.info(CC.translate(StringUtils.replacePlaceholders(prefix + " " + str,placeholders)));
    }
    public static void warn(Object str,Object... placeholders){
        instance.actualLogger.warning(CC.translate(StringUtils.replacePlaceholders(prefix + " " + str,placeholders)));
    }
    public static void error(Object str,Object... placeholders){
        instance.actualLogger.severe(CC.translate(StringUtils.replacePlaceholders(prefix + " " + str,placeholders)));
    }
    public static void severe(Object str,Object... placeholders){
        error(str, placeholders);
    }
    public static void debug(Object str,Object... placeholders){
        info(debugPrefix + " " + StringUtils.replacePlaceholders(str.toString(),placeholders));
    }
}
