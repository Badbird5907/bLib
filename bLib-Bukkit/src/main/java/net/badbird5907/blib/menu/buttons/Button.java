package net.badbird5907.blib.menu.buttons;

import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;


public abstract class Button {

    public abstract ItemStack getItem(Player player);
    public abstract int getSlot();

    public void onClick(Player player, int slot, ClickType clickType) {

    }

    public int[] getSlots() {
        return null;
    }


    public boolean hasSlot(int slot) {
        return slot == this.getSlot() || this.getSlots() != null && Arrays.stream(this.getSlots()).anyMatch(i -> i == slot);
    }

    public static boolean hasSlot(List<Button> buttons, int value) {
        return buttons.stream()
                .filter(slot -> slot.getSlot() == value || slot.getSlots() != null
                        && Arrays.stream(slot.getSlots()).anyMatch(i -> i == value))
                .findFirst().orElse(null) != null;
    }

    public static Button getGlass(int slot) {
        return new Button() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short) 7).build();
            }

            @Override
            public int getSlot() {
                return slot;
            }
        };
    }
}
