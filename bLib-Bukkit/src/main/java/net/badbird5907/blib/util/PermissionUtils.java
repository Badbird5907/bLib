package net.badbird5907.blib.util;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionUtils {
    public static Set<String> getRegisteredPermissionsString(){
        Set<String> permissions = new HashSet<>();
        Bukkit.getPluginManager().getPermissions().forEach(perm -> permissions.add(perm.getName()));
        return permissions;
    }
}
