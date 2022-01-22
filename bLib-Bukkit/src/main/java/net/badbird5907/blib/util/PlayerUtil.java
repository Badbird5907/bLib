package net.badbird5907.blib.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.MINUTES;
import static net.badbird5907.blib.util.Logger.debug;
import static org.bukkit.Bukkit.getOfflinePlayer;

public class PlayerUtil {
	private static final CacheLoader<UUID, String> loader = new CacheLoader<UUID, String>() {
		@Override
		public String load(UUID key) {
			return getOfflinePlayer(key).getName();
		}
	};

	private static final LoadingCache<UUID, String> cache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterWrite(30, MINUTES).build(loader);

	/**
	 * gets the name of a player online or offline
	 */
	public static String getPlayerName(UUID uuid) {
		try {
			return cache.getUnchecked(uuid);
		} catch (Exception e) { // I don't want to deal with any cache related stuff
			debug("Error getting player name from cache: " + e.getMessage() + " operation will still continue normally.");
		}
		return getOfflinePlayer(uuid).getName();
	}

	/**
	 * gets the {@link UUID} of a player online or offline
	 *
	 * @param name
	 * @return
	 */
	public static UUID getPlayerUUID(String name) {
		return getOfflinePlayer(name).getUniqueId();
	}
}
