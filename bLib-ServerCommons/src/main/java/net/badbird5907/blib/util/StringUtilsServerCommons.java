package net.badbird5907.blib.util;

import static java.lang.Math.round;
import static net.md_5.bungee.api.ChatColor.stripColor;
import static org.apache.commons.lang3.StringUtils.repeat;

public class StringUtilsServerCommons {
	public static String centerText(String text) {
		int maxWidth = 72, //TODO tweak this
				spaces = (int) round((maxWidth - 1.4 * stripColor(text).length()) / 2);
		return repeat(" ", spaces) + text;
	}
}
