package net.badbird5907.blib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class SimpleEvent extends Event {
    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }
}
