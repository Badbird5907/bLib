package net.badbird5907.blib.utils;

public class StringUtils {
	public static String replacePlaceholders(final String str, final Object... replace){
		if ((replace == null) || (replace.length == 0)) return str;
		String finalReturn = str;
		if ((replace == null) || (replace.length == 0)) return finalReturn;
		int i = 0;
		for (Object s : replace) {
			i++;
			String toReplace = "%" + i;
			finalReturn = finalReturn.replace(toReplace, s.toString());
		}
		return finalReturn;
	}
}
