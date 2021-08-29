package net.badbird5907.blib.util;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;

public class StringUtilsServerCommons {
    public static String centerText(String text) {
        int maxWidth = 72, //TODO tweak this
                spaces = (int) Math.round((maxWidth-1.4* ChatColor.stripColor(text).length())/2);
        return StringUtils.repeat(" ", spaces)+text;
    }
}
