package net.badbird5907.blib;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.blib.command.CommandFramework;
import net.badbird5907.blib.menu.MenuListener;
import net.badbird5907.blib.util.Glow;
import net.badbird5907.blib.util.Logger;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import static net.badbird5907.blib.util.ReflectionUtils.getClassesInPackage;
import static net.badbird5907.blib.util.Tasks.init;
import static org.bukkit.Bukkit.getPluginManager;

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
	@Getter
	private static CommandFramework commandFramework;

	public bLib(Plugin plugin, String prefix) {
		instance = this;
		setPlugin(plugin);
		new Logger(plugin.getLogger(), prefix, "[DEBUG]");
		init(plugin);
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
		getClassesInPackage(plugin, p).forEach(this::registerListener);
	}

	@SneakyThrows
	public void registerListener(Class clazz) {
		if (Listener.class.isAssignableFrom(clazz))
			getPluginManager().registerEvents((Listener) clazz.getDeclaredConstructor().newInstance(), plugin);
	}
}
