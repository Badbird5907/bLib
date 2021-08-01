package net.badbird5907.blib.util;

import java.util.List;

import static net.md_5.bungee.api.ChatColor.*;
import static java.util.stream.Collectors.*;

public final class CC {
	public static final String WHITE = WHITE.toString(), GREEN = GREEN.toString(), D_GREEN = DARK_GREEN.toString(), D_BLUE = DARK_BLUE.toString(), RED = RED.toString(), D_RED = DARK_RED.toString(), GRAY = GRAY.toString(), D_GRAY = DARK_GRAY.toString(), YELLOW = YELLOW.toString(), GOLD = GOLD.toString(), AQUA = AQUA.toString(), D_AQUA = DARK_AQUA.toString(), BLUE = BLUE.toString(), PINK = LIGHT_PURPLE.toString(), PURPLE = DARK_PURPLE.toString(), BLACK = BLACK.toString();
	public static final String B = BOLD.toString(), BOLD = BOLD.toString(), I = ITALIC.toString(), ITALIC = ITALIC.toString(), U = UNDERLINE.toString(), UNDERLINE = UNDERLINE.toString(), S = STRIKETHROUGH.toString(), STRIKETHROUGH = STRIKETHROUGH.toString(), R = RESET.toString(), RESET = RESET.toString();
	public static final String PRIMARY = AQUA.toString(), SECONDARY = LIGHT_PURPLE.toString(), ACCENT = DARK_AQUA.toString();
	public static final String SPLITTER = "┃", SCOREBOARD_SEPARATOR = GRAY + S + "--------------------", SCOREBOARD_IP_SEPARATOR = GRAY + S + "---", SEPARATOR = GRAY + S + "-------------------------------------", BULLET = "•", NEWLINE = "\n", NL = "\n", ARROW_RIGHT = "»", ARROW_LEFT = "«", X = "✘", CHECK = "\u2714", SELECTOR_ARROW = "\u25b8";

	public static String translate(String in) {
		return translateAlternateColorCodes('&', in);
	}

	public static List<String> translate(List<String> input) {
		return input.stream().map(CC::translate).collect(toList());
	}

	public static String strip(String in) {
		return stripColor(in);
	}

	private CC() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
