package net.badbird5907.blib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CUtil {
    public static Component gray(String text) {return Component.text(text, NamedTextColor.GRAY);}
    public static Component green(String text) {return Component.text(text, NamedTextColor.GREEN);}
    public static Component dGreen(String text) {return Component.text(text, NamedTextColor.DARK_GREEN);}
    public static Component darkGreen(String text) {return Component.text(text, NamedTextColor.DARK_GREEN);}
    public static Component dBlue(String text) {return Component.text(text, NamedTextColor.DARK_BLUE);}
    public static Component darkBlue(String text) {return Component.text(text, NamedTextColor.DARK_BLUE);}
    public static Component darkRed(String text) {return Component.text(text, NamedTextColor.DARK_RED);}
    public static Component red(String text) {return Component.text(text, NamedTextColor.RED);}
    public static Component dRed(String text) {return Component.text(text, NamedTextColor.DARK_RED);}
    public static Component darkGray(String text) {return Component.text(text, NamedTextColor.DARK_GRAY);}
    public static Component dGray(String text) {return Component.text(text, NamedTextColor.DARK_GRAY);}
    public static Component yellow(String text) {return Component.text(text, NamedTextColor.YELLOW);}
    public static Component gold(String text) {return Component.text(text, NamedTextColor.GOLD);}
    public static Component aqua(String text) {return Component.text(text, NamedTextColor.AQUA);}
    public static Component darkAqua(String text) {return Component.text(text, NamedTextColor.DARK_AQUA);}
    public static Component dAqua(String text) {return Component.text(text, NamedTextColor.DARK_AQUA);}
    public static Component blue(String text) {return Component.text(text, NamedTextColor.BLUE);}
    public static Component pink(String text) {return Component.text(text, NamedTextColor.LIGHT_PURPLE);}
    public static Component purple(String text) {return Component.text(text, NamedTextColor.DARK_PURPLE);}
    public static Component black(String text) {return Component.text(text, NamedTextColor.BLACK);}

    public static Component create(String text, Style... styles) {
        Component component = Component.text(text);
        if (styles != null && styles.length > 0) {
            for (Style style : styles) {
                component = component.style(style);
            }
        }
        return component;
    }

    public static Component deseializeSection(String s) {
        return LegacyComponentSerializer.legacySection().deserialize(s);
    }

    public static List<Component> fromStringList(List<String> list) {
        return new ArrayList<>(list.stream().map(text -> LegacyComponentSerializer.legacySection().deserialize(CC.translate(text))).toList());
    }
    public static List<Component> fromStringArray(String[] strArray) {
        return fromStringList(Arrays.asList(strArray));
    }
}
