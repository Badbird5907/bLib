package net.badbird5907.blib.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Getter
@Setter
/**
 * a class for storing locations with gson instead of using {@link Location}
 */
public class StoredLocation {
	private double x, y, z;
	private String world;
	private UUID worldId;
	private float pitch, yaw;

	public StoredLocation(Location loc) {
		this.x = loc.getBlockX();
		this.y = loc.getBlockY();
		this.z = loc.getBlockZ();
		this.world = requireNonNull(loc.getWorld()).getName();
		this.worldId = loc.getWorld().getUID();
		this.pitch = loc.getPitch();
		this.yaw = loc.getYaw();
	}

	public World getWorld() {
		return Bukkit.getWorld(worldId);
	}

	public StoredLocation center() {
		Location location = getLocation();
		//location.add(x > 0 ? 0.5 : -0.5, 0.0, z > 0 ? 0.5 : -0.5);
		//location.add(0,0,z > 0 ?
		//        1 : //positive axis
		//        1); //negative axis
		location.add(x > 0 ? 0.5 : -0.5, 0, 0);
		return new StoredLocation(location);
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(worldId), x, y, z, yaw, pitch);
	}
}
