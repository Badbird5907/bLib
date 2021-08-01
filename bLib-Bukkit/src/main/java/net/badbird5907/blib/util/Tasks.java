package net.badbird5907.blib.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Tasks {
	private static Plugin plugin;

	public Tasks(Plugin plugin1) {
		plugin = plugin1;
	}

	public static void run( Runnable callable) {
		Bukkit.getScheduler().runTask(plugin, callable);
	}

	public static void runSync(Runnable callable) {
		run(callable);
	}

	public static void runAsync( Runnable callable) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, callable);
	}

	public static void runLater( Runnable callable, long delay) {
		Bukkit.getScheduler().runTaskLater(plugin, callable, delay);
	}

	public static void runAsyncLater( Runnable callable, long delay) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, callable, delay);
	}

	public static void runTimer( Runnable callable, long delay, long interval) {
		Bukkit.getScheduler().runTaskTimer(plugin, callable, delay, interval);
	}

	public static void runAsyncTimer( Runnable callable, long delay, long interval) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, callable, delay, interval);
	}
}
