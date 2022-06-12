package net.badbird5907.blib.util;

import java.util.HashMap;
import java.util.UUID;

public class Cooldown {
    private static HashMap<String, HashMap<UUID, Long>> cooldown = new HashMap<>();

    public static void createCooldown(String k) {
        if (cooldown.containsKey(k.toLowerCase()))
            return;
        cooldown.put(k.toLowerCase(), new HashMap<>());
    }

    public static HashMap<UUID, Long> getCooldownMap(String k) {
        if (cooldown.containsKey(k.toLowerCase()))
            return cooldown.get(k.toLowerCase());
        return null;
    }

    public static void addCooldown(String k, UUID p, double seconds) {
        if (!cooldown.containsKey(k.toLowerCase()))
            cooldown.put(k.toLowerCase(), new HashMap<>());
        long next = System.currentTimeMillis() + (long) (seconds * 1000);
        cooldown.get(k.toLowerCase()).put(p, next);
    }

    public static boolean isOnCooldown(String k, UUID p) {
        return (cooldown.containsKey(k.toLowerCase()) && cooldown.get(k.toLowerCase()).containsKey(p) &&
                System.currentTimeMillis() <= ((Long) ((HashMap) cooldown.get(k.toLowerCase())).get(p)).longValue());
    }

    public static double getCooldownForPlayerDouble(String k, UUID p) {
        long time = getCooldownForPlayerLong(k, p);
        return time / 1000.0d;
    }

    public static int getCooldownForPlayerInt(String k, UUID p) {
        return (int) (((Long) ((HashMap) cooldown.get(k.toLowerCase())).get(p)).longValue() -
                System.currentTimeMillis()) / 1000;
    }

    public static long getCooldownForPlayerLong(String k, UUID p) {
        return (((Long) ((HashMap) cooldown.get(k.toLowerCase())).get(p)).longValue() -
                System.currentTimeMillis());
    }

    public static void removeCooldown(String k, UUID p) {
        if (!cooldown.containsKey(k.toLowerCase()))
            cooldown.put(k.toLowerCase(), new HashMap<>());
        if (cooldown.get(k.toLowerCase()).containsKey(p))
            ((HashMap) cooldown.get(k.toLowerCase())).remove(p);
    }

    public static boolean wasOnCooldown(String k, UUID p) {
        if (!cooldown.containsKey(k.toLowerCase()))
            cooldown.put(k.toLowerCase(), new HashMap<>());
        return ((HashMap) cooldown.get(k.toLowerCase())).containsKey(p);
    }

    public static boolean cooldownExists(String k) {
        return cooldown.containsKey(k);
    }
}
