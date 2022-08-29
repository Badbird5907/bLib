package net.badbird5907.blib;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.blib.command.CommandFramework;
import net.badbird5907.blib.menu.MenuListener;
import net.badbird5907.blib.util.Glow;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.ReflectionUtils;
import net.badbird5907.blib.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@Getter
@Setter
public class bLib {
	@Getter
	@Setter
	private static bLib instance;
	@Getter
	@Setter
	private static Plugin plugin;
	@Getter
	@Setter
	private static boolean autoCompleteCommandsFromUsage = false;
	/**
	 * @deprecated Please use https://github.com/OctoPvP/Commander/
	 */
	@Getter
	private static CommandFramework commandFramework;

	public bLib(Plugin plugin, String prefix) {
		instance = this;
		setPlugin(plugin);
		new Logger(plugin.getLogger(), prefix, "[DEBUG]");
		Tasks.init(plugin);
		commandFramework = new CommandFramework(plugin);
		plugin.getServer().getPluginManager().registerEvents(new MenuListener(), plugin);
		Glow.init(plugin);
	}

	public static bLib create(Plugin plugin) {
		return new bLib(plugin, "");
	}

	public static bLib create(Plugin plugin, String prefix) {
		return new bLib(plugin, prefix);
	}

	@Deprecated
	public bLib setAutoCompleteCommands(boolean b) {
		autoCompleteCommandsFromUsage = b;
		return this;
	}

	@SneakyThrows
	public void registerListenersInPackage(String p) {
		ReflectionUtils.getClassesInPackage(plugin, p).forEach(this::registerListener);
	}

	@SneakyThrows
	public void registerListener(Class clazz) {
		if (Listener.class.isAssignableFrom(clazz))
			Bukkit.getPluginManager().registerEvents((Listener) clazz.getDeclaredConstructor().newInstance(), plugin);
	}
}
