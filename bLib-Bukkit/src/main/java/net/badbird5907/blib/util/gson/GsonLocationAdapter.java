package net.badbird5907.blib.util.gson;

import com.google.gson.*;
import org.bukkit.Location;

import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Bukkit.getWorlds;

public class GsonLocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {
	public static final GsonLocationAdapter INSTANCE = new GsonLocationAdapter();

	@Override
	public Location deserialize(JsonElement jsonString, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		assert jsonString.isJsonObject() : "Invalid json string";
		final JsonObject obj = (JsonObject) jsonString;
		final JsonElement worldId = obj.get("worldId");
		final JsonElement x = obj.get("x");
		final JsonElement y = obj.get("y");
		final JsonElement z = obj.get("z");
		final JsonElement yaw = obj.get("yaw");
		final JsonElement pitch = obj.get("pitch");
		assert worldId != null && x != null && y != null && z != null : "String json mal formada!";
		assert worldId.isJsonPrimitive() && ((JsonPrimitive) worldId).isString() : "World is not a string!";
		assert x.isJsonPrimitive() && ((JsonPrimitive) x).isNumber() : "X is not a number!";
		assert y.isJsonPrimitive() && ((JsonPrimitive) y).isNumber() : "Y is not a number!";
		assert z.isJsonPrimitive() && ((JsonPrimitive) z).isNumber() : "Z is not a number!";
		assert yaw == null || (yaw.isJsonPrimitive() && ((JsonPrimitive) yaw).isNumber()) : "Yaw is not a number!";
		assert pitch == null || (pitch.isJsonPrimitive() && ((JsonPrimitive) pitch).isNumber()) : "Pitch is not a number!";
		return new Location(getWorlds().get(0), x.getAsDouble(), y.getAsDouble(), z.getAsDouble(), yaw != null ? yaw.getAsFloat() : 0.0F, pitch != null ? pitch.getAsFloat() : 0.0F);
	}

	@Override
	public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject obj = new JsonObject();
		obj.addProperty("worldId", requireNonNull(location.getWorld()).getUID().toString());
		obj.addProperty("x", location.getX());
		obj.addProperty("y", location.getY());
		obj.addProperty("z", location.getZ());
		obj.addProperty("yaw", location.getYaw());
		obj.addProperty("pitch", location.getPitch());
		return obj;
	}
}