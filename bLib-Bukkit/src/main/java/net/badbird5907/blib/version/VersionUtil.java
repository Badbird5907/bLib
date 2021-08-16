package net.badbird5907.blib.version;

import org.bukkit.Bukkit;

public class VersionUtil {
    private static final String SERVER_VERSION;
    static {
        String name = Bukkit.getServer().getClass().getName();
        name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
        name = name.substring(0, name.indexOf("."));
        SERVER_VERSION = name;
    }

    public static boolean isVersionHigherThan(int mainVersion, int secondVersion) {
        String firstChar = SERVER_VERSION.substring(1, 2);
        int fInt = Integer.parseInt(firstChar);
        if (fInt < mainVersion)
            return false;
        StringBuilder secondChar = new StringBuilder();
        for (int i = 3; i < 10; i++) {
            if (SERVER_VERSION.charAt(i) == '_' || SERVER_VERSION.charAt(i) == '.')
                break;
            secondChar.append(SERVER_VERSION.charAt(i));
        }

        int sInt = Integer.parseInt(secondChar.toString());
        if (sInt <= secondVersion)
            return false;
        return true;
    }
    public static Version getVersion(){
        return Version.getVersion("V_" + Bukkit.getBukkitVersion().split("-")[0].replace(".","_"));
    }
    public static boolean over1_13(){
        return isVersionHigherThan(1,13);
    }
}
