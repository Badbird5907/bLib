package net.badbird5907.blib.menu.buttons.impl;

import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CloseButton extends Button {
    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(XMaterial.BARRIER.parseItem()).name(CC.RED + "Close").build();
    }

    @Override
    public int getSlot() {
        return 40;
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType) {
        super.onClick(player, slot, clickType);
        player.getOpenInventory().close();
    }
}
