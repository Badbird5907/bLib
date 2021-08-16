package net.badbird5907.blib.menu.buttons.impl;

import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.menu.PaginatedMenu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PreviousPageButton extends Button {
    private final PaginatedMenu paginatedMenu;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.ARROW);
        item.setName("&aPrevious page");
        if (this.paginatedMenu.getPage() < this.paginatedMenu.getPages(player)) { //next page
            item.lore(
                    CC.GREEN + "Click to go to the last page"
            );
        }else item.lore(CC.RED + "This is the first page!");
        item.name(CC.GREEN + "Previous Page");
        return item.build();
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType) {
        if (this.paginatedMenu.getPage() == 1) {
            player.sendMessage(CC.RED + "You're already on the first page!");
            return;
        }
        this.paginatedMenu.changePage(player, -1);
    }

    @Override
    public int getSlot() {
        return 36;
    }
}
