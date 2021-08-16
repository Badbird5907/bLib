package net.badbird5907.blib.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerUtil {
    private static CacheLoader<UUID,String> loader = new CacheLoader<UUID,String>() {
        @Override
        public String load(UUID key) {
            return Bukkit.getOfflinePlayer(key).getName();
        }
    };

    private static LoadingCache<UUID,String> cache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterWrite(30, TimeUnit.MINUTES).build(loader);
    /**
     * gets the name of a player online or offline
     * @param uuid
     * @return
     */
    public static String getPlayerName(UUID uuid){
        try{
            return cache.getUnchecked(uuid);
        } catch (Exception e) {//i dont want to deal with any cache related shit
            Logger.debug("Error getting player name from cache: " + e.getMessage() + " operation will still continue normally.");
        }
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    /**
     * gets the {@link UUID} of a player online or offline
     * @param name
     * @return
     */
    public static UUID getPlayerUUID(String name){
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }
}
