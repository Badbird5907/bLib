package net.badbird5907.blib.utils;

public class StringUtils {
	public static String replacePlaceholders(final String str, final Object... replace) {
		if (replace == null || replace.length == 0) {
			return str;
		}
		int i = 0;
		String finalReturn = str;
		for (Object s : replace) {
			i++;
			String toReplace = "%" + i;
			finalReturn = finalReturn.replace(toReplace, s.toString());
		}
		return finalReturn;
	}
}
