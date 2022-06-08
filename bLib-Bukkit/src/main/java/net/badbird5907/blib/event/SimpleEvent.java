package net.badbird5907.blib.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import static org.bukkit.Bukkit.getPluginManager;

public class SimpleEvent extends Event {
	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public void call() {
		getPluginManager().callEvent(this);
	}
}
