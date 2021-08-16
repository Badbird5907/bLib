package net.badbird5907.blib.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

@Getter
@Setter
/**
 * a class for storing locations with gson instead of using {@link Location}
 */
public class StoredLocation {
    public StoredLocation(Location loc){
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.world = loc.getWorld().getName();
        this.worldId = loc.getWorld().getUID();
    }
    private int x,y,z;
    private String world;
    private UUID worldId;

    public World getWorld() {
        return Bukkit.getWorld(worldId);
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(worldId),x,y,z);
    }
}
