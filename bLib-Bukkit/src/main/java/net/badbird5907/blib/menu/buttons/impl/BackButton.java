package net.badbird5907.blib.menu.buttons.impl;

import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BackButton extends Button {
    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(XMaterial.ARROW.parseItem()).name(CC.GREEN + "Back").build();
    }

    @Override
    public int getSlot() {
        return 39;
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
        clicked(player, slot, clickType, event);
    }
    public abstract void clicked(Player player,int slot,ClickType clickType, InventoryClickEvent event);
}
