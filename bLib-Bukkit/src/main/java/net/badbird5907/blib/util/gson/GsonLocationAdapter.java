package net.badbird5907.blib.util.gson;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.UUID;

public class GsonLocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {
    public static final GsonLocationAdapter INSTANCE = new GsonLocationAdapter();
    @Override
    public Location deserialize(JsonElement jsonString, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!jsonString.isJsonObject()) {
            throw new JsonParseException("Invalid json string");
        }
        final JsonObject obj = (JsonObject) jsonString;
        final JsonElement worldId = obj.get("worldId");
        final JsonElement x = obj.get("x");
        final JsonElement y = obj.get("y");
        final JsonElement z = obj.get("z");
        final JsonElement yaw = obj.get("yaw");
        final JsonElement pitch = obj.get("pitch");

        if (worldId == null || x == null || y == null || z == null) {
            throw new JsonParseException("String json mal formada!");
        }
        if (!worldId.isJsonPrimitive() || !((JsonPrimitive) worldId).isString()) {
            throw new JsonParseException("World is not a string!");
        }
        if (!x.isJsonPrimitive() || !((JsonPrimitive) x).isNumber()) {
            throw new JsonParseException("X is not a number!");
        }
        if (!y.isJsonPrimitive() || !((JsonPrimitive) y).isNumber()) {
            throw new JsonParseException("Y is not a number!");
        }
        if (!z.isJsonPrimitive() || !((JsonPrimitive) z).isNumber()) {
            throw new JsonParseException("Z is not a number!");
        }
        if (yaw != null && (!yaw.isJsonPrimitive() || !((JsonPrimitive) yaw).isNumber())) {
            throw new JsonParseException("Yaw is not a number!");
        }
        if (pitch != null && (!pitch.isJsonPrimitive() || !((JsonPrimitive) pitch).isNumber())) {
            throw new JsonParseException("Pitch is not a number!");
        }
        return new Location(Bukkit.getWorlds().get(0), x.getAsDouble(), y.getAsDouble(), z.getAsDouble(),
                yaw != null ? yaw.getAsFloat() : 0.0F,
                pitch != null ?pitch.getAsFloat() : 0.0F);
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {

        final JsonObject obj = new JsonObject();
        obj.addProperty("worldId", location.getWorld().getUID().toString());
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        obj.addProperty("yaw", location.getYaw());
        obj.addProperty("pitch", location.getPitch());
        return obj;

    }

}