package net.badbird5907.blib.menu.buttons;

import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static java.util.Arrays.stream;
import static net.badbird5907.blib.util.XMaterial.GRAY_STAINED_GLASS_PANE;


public abstract class Button {

	public static boolean hasSlot(List<Button> buttons, int value) {
		return buttons.stream().filter(slot -> (slot.getSlot() == value) || ((slot.getSlots() != null) && stream(slot.getSlots()).anyMatch(i -> i == value))).findFirst().orElse(null) != null;
	}

	public static Button getGlass(int slot) {
		return new Button() {
			@Override
			public ItemStack getItem(Player player) {
				return new ItemBuilder(GRAY_STAINED_GLASS_PANE.parseMaterial()).durability((short) 7).build();
			}

			@Override
			public int getSlot() {
				return slot;
			}
		};
	}

	public abstract ItemStack getItem(Player player);

	public abstract int getSlot();

	public void onClick(Player player, int slot, ClickType clickType) {
	}

	public int[] getSlots() {
		return null;
	}

	public boolean hasSlot(int slot) {
		return (slot == this.getSlot()) || ((this.getSlots() != null) && stream(this.getSlots()).anyMatch(i -> i == slot));
	}
}
