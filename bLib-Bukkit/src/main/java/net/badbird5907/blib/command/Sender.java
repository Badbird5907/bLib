package net.badbird5907.blib.command;

import net.badbird5907.blib.utils.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

import static java.util.Arrays.stream;
import static net.badbird5907.blib.util.CC.translate;

/**
 * extension of {@link CommandSender} to make stuff (placeholders in messages) easier (ripped out of octopvp lmao)
 */
public class Sender implements CommandSender {
	private CommandSender commandSender;

	public Sender(CommandSender commandSender) {
		this.commandSender = commandSender;
	}

	@Override
	public void sendMessage(String s) {
		commandSender.sendMessage(translate(s));
	}

	@Override
	public void sendMessage(String[] strings) {
		stream(strings).forEach(string -> commandSender.sendMessage(translate(string)));
	}

	@Override
	public void sendMessage(UUID sender, String message) {
		commandSender.sendMessage(sender, translate(message));
	}

	@Override
	public void sendMessage(UUID sender, String[] messages) {
		commandSender.sendMessage(sender, messages);
	}

	public void sendMessage(Object message, Object... placeholders) {
		commandSender.sendMessage(StringUtils.replacePlaceholders(translate(message.toString()), placeholders));
	}

	@Override
	public Server getServer() {
		return commandSender.getServer();
	}

	@Override
	public String getName() {
		return commandSender.getName();
	}

	@Override
	public Spigot spigot() {
		return commandSender.spigot();
	}

	@Override
	public @NotNull Component name() {
		return commandSender.name();
	}

	@Override
	public boolean isPermissionSet(String s) {
		return commandSender.isPermissionSet(s);
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		return commandSender.isPermissionSet(permission);
	}

	@Override
	public boolean hasPermission(String s) {
		return commandSender.hasPermission(s);
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return commandSender.hasPermission(permission);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
		return commandSender.addAttachment(plugin, s, b);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return commandSender.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
		return commandSender.addAttachment(plugin, s, b, i);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int i) {
		return commandSender.addAttachment(plugin, i);
	}

	@Override
	public void removeAttachment(PermissionAttachment permissionAttachment) {
		commandSender.removeAttachment(permissionAttachment);
	}

	@Override
	public void recalculatePermissions() {
		commandSender.recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return commandSender.getEffectivePermissions();
	}

	@Override
	public boolean isOp() {
		return commandSender.isOp();
	}

	@Override
	public void setOp(boolean b) {
		commandSender.setOp(b);
	}

	public Player getPlayer() {
		try {
			return Bukkit.getPlayer(getName());
		} catch (Exception e) {
			return null;
		}
	}

	public String getDisplayName() {
		return (getPlayer() == null) ? "CONSOLE" : getPlayer().getDisplayName();
	}

	public CommandSender getCommandSender() {
		return this.commandSender;
	}

	public void setCommandSender(CommandSender commandSender) {
		this.commandSender = commandSender;
	}
}
