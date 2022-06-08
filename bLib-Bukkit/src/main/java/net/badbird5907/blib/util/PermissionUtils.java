package net.badbird5907.blib.util;

import org.bukkit.permissions.Permission;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.bukkit.Bukkit.getPluginManager;

public class PermissionUtils {
	public static Set<String> getRegisteredPermissionsString() {
		return getPluginManager().getPermissions().stream().map(Permission::getName).collect(toSet());
	}
}
