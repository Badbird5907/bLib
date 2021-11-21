package net.badbird5907.blib.menu.buttons.impl;

import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.menu.PaginatedMenu;
import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import static net.badbird5907.blib.util.CC.GREEN;
import static net.badbird5907.blib.util.CC.RED;
import static net.badbird5907.blib.util.XMaterial.ARROW;

@RequiredArgsConstructor
public class NextPageButton extends Button {
	private final PaginatedMenu paginatedMenu;

	@Override
	public ItemStack getItem(Player player) {
		ItemBuilder item = new ItemBuilder(ARROW.parseItem());
		//next page
		item.lore((this.paginatedMenu.getPage() < this.paginatedMenu.getPages(player)) ? (GREEN + "Click to go to the next page") : (RED + "This is the last page!")).name(GREEN + "Next Page");
		return item.build();
	}

	@Override
	public void onClick(Player player, int slot, ClickType clickType) {
		if (!(this.paginatedMenu.getPage() < this.paginatedMenu.getPages(player))) {
			player.sendMessage(RED + "You're already on the last page!");
			return;
		}
		this.paginatedMenu.changePage(player, 1);
	}

	@Override
	public int getSlot() {
		return 44;
	}
}
