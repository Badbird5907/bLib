package net.badbird5907.blib.util;

import java.util.HashMap;
import java.util.UUID;

public class Cooldown {
	private static HashMap<String, HashMap<UUID, Long>> cooldown = new HashMap<>();

	public static void createCooldown(String k) {
		System.out.println("Debug: creating cooldown: " + k);
		if (cooldown.containsKey(k.toLowerCase())) throw new IllegalArgumentException("Cooldown already exists. Cooldown: " + k);
		cooldown.put(k.toLowerCase(), new HashMap<>());
	}

	public static HashMap<UUID, Long> getCooldownMap(String k) {
		if (cooldown.containsKey(k.toLowerCase())) return cooldown.get(k.toLowerCase());
		return null;
	}

	public static void addCooldown(String k, UUID p, int seconds) {
		if (!cooldown.containsKey(k.toLowerCase())) throw new IllegalArgumentException(k.toLowerCase() + " does not exist");
		long next = System.currentTimeMillis() + seconds * 1000L;
		cooldown.get(k.toLowerCase()).put(p, Long.valueOf(next));
	}

	public static boolean isOnCooldown(String k, UUID p) {
		return (cooldown.containsKey(k.toLowerCase()) && cooldown.get(k.toLowerCase()).containsKey(p) && System.currentTimeMillis() <= ((Long) ((HashMap) cooldown.get(k.toLowerCase())).get(p)).longValue());
	}

	public static int getCooldownForPlayerInt(String k, UUID p) {
		return (int) (((Long) ((HashMap) cooldown.get(k.toLowerCase())).get(p)).longValue() - System.currentTimeMillis()) / 1000;
	}

	public static long getCooldownForPlayerLong(String k, UUID p) {
		return (int) (((Long) ((HashMap) cooldown.get(k.toLowerCase())).get(p)).longValue() - System.currentTimeMillis());
	}

	public static void removeCooldown(String k, UUID p) {
		if (!cooldown.containsKey(k.toLowerCase())) throw new IllegalArgumentException(k.toLowerCase() + " does not exist");
		if (cooldown.get(k.toLowerCase()).containsKey(p)) ((HashMap) cooldown.get(k.toLowerCase())).remove(p);
	}
	public static boolean wasOnCooldown(String k, UUID p) {
		if (!cooldown.containsKey(k.toLowerCase())) throw new IllegalArgumentException(k.toLowerCase() + " does not exist");
		return ((HashMap) cooldown.get(k.toLowerCase())).containsKey(p);
	}
	public static boolean cooldownExists(String k) {
		return cooldown.containsKey(k);
	}
}
