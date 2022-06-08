package net.badbird5907.blib.command;

import net.badbird5907.blib.annotation.Disable;
import net.badbird5907.blib.bLib;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.List;

public abstract class BaseCommand {
	public Plugin plugin = bLib.getPlugin();
	private String usageMessage = "";

	public BaseCommand() {
		if (this.getClass().isAnnotationPresent(Disable.class)) return;
		CommandFramework.getInstance().registerCommands(this);
	}

	public abstract CommandResult execute(Sender sender, String[] args);

	public List<String> tabComplete(Sender sender, String[] args) {
		return null;
	}

	public String getUsageMessage() {
		return usageMessage;
	}

	public void sendUsage(Sender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getClass().getAnnotation(Command.class).usage()));
	}
}
