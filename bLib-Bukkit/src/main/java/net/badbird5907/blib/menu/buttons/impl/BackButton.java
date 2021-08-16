package net.badbird5907.blib.menu.buttons.impl;

import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class BackButton extends Button {
    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.ARROW).name(CC.GREEN + "Back").build();
    }

    @Override
    public int getSlot() {
        return 39;
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType) {
        clicked(player, slot, clickType);
    }
    public abstract void clicked(Player player,int slot,ClickType clickType);
}
