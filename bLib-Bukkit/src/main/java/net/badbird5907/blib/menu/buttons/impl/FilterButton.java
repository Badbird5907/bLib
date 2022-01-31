package net.badbird5907.blib.menu.buttons.impl;

import net.badbird5907.blib.menu.buttons.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class FilterButton extends Button {
    @Override
    public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
        clicked(player, clickType, slot, event);
    }

    public abstract void clicked(Player player, ClickType type, int slot, InventoryClickEvent event);

    @Override
    public int getSlot() {
        return 37;
    }
}
