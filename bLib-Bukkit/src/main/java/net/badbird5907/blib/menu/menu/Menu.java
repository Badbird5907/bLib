package net.badbird5907.blib.menu.menu;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.bLib;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.menu.MenuManager;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.impl.CloseButton;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public abstract class Menu {
    public Plugin plugin = bLib.getPlugin();

    @Getter
    private List<Button> buttons = new ArrayList<>();

    @Getter
    @Setter
    private boolean autoUpdate = false;

    @Getter
    @Setter
    @Deprecated
    private boolean updateAsynchronously = false;

    public abstract List<Button> getButtons(Player player);

    public abstract String getName(Player player);

    public List<Button> getFinalButtons(Player player) {
        List<Button> list = getButtons(player);
        if (list == null)
            list = new ArrayList<>();
        List<Button> l = new ArrayList<>(list);
        if (this instanceof PaginatedMenu)
            return l;
        Button backButton = getBackButton(player);
        if (backButton != null)
            l.add(backButton);
        return l;
    }

    @Getter
    @Setter
    private boolean cancel = true;


    @Getter
    @Setter
    public Menu previous;

    public void open(Sender sender) {
        open(sender.getPlayer());
    }

    public void open(Player player) {
        try {
            Menu previous = MenuManager.getOpenedMenus().get(player.getUniqueId());
            if (previous != null && previous != this) {
                setPrevious(previous);
                previous.onCloseReserved(player);
                MenuManager.getOpenedMenus().remove(player.getUniqueId());
            }

            this.buttons = this.getFinalButtons(player);
            String title = this.getName(player);
            if (title == null)
                title = "";
            if (title.length() > 32) title = title.substring(0, 32);
            title = CC.translate(title);

            if (player.getOpenInventory() != null) {
                player.closeInventory();
            }

            Inventory inventory = Bukkit.createInventory(player, this.getInventorySize(this.buttons), title);

            this.buttons.forEach(button -> {
                System.out.println(button.getItem(player));
                if (button.getSlot() >= 0)
                    inventory.setItem(button.getSlot(), button.getItem(player));
                if (button.getSlots() != null) {
                    Arrays.stream(button.getSlots()).forEach(extra -> {
                        if (shouldKeepExtra(extra)) inventory.setItem(extra, button.getItem(player));
                    });
                }
            });

            MenuManager.getOpenedMenus().put(player.getUniqueId(), this);
            player.openInventory(inventory);
            onOpenReserved(player);
            this.onOpen(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean shouldKeepExtra(int slot) {
        if (slot < 0)
            return false;
        for (Button button1 : this.buttons) {
            if (button1.getSlot() == slot)
                return false;
            else if (Lists.newArrayList(button1.getSlots()).contains(slot))
                return true;
        }
        return true;
    }

    public Button getCloseButton() {
        return new CloseButton();
    }

    public void update(Player player) {
        try {
            this.buttons = this.getFinalButtons(player);
            String title = this.getName(player);
            if (title == null)
                title = "";

            if (title.length() > 32) title = title.substring(0, 32);
            title = CC.translate(title);

            boolean passed = false, paginated = this instanceof PaginatedMenu;
            Inventory inventory = null;
            Menu currentlyOpenedMenu = MenuManager.getOpenedMenus().get(player.getUniqueId());
            Inventory current = player.getOpenInventory().getTopInventory();
            String currentName = currentlyOpenedMenu == null ? null : currentlyOpenedMenu.getName(player);
            boolean a1 = currentlyOpenedMenu != null,
                    a2 = CC.translate(currentName).equals(player.getOpenInventory().getTitle()),
                    a3 = current.getSize() == this.getInventorySize(this.buttons);
            if (a1 && a3) {
                if (a2) {
                    inventory = current;
                    passed = true;
                } else if (paginated) {
                    inventory = current;
                    passed = true;
                }
            }

            if (inventory == null) {
                inventory = Bukkit.createInventory(player, this.getInventorySize(this.buttons), title);
            }

            /**
             * TemporaryInventory
             * Used to prevent item flickering because 'inventory' is live player inventory
             */
            Inventory temporaryInventory = Bukkit.createInventory(player, inventory.getSize(), currentName);

            this.buttons.forEach(slot -> {
                if (slot.getSlot() >= 0)
                    temporaryInventory.setItem(slot.getSlot(), slot.getItem(player));

                if (slot.getSlots() != null) {
                    Arrays.stream(slot.getSlots()).forEach(extra -> {
                        if (shouldKeepExtra(extra)) temporaryInventory.setItem(extra, slot.getItem(player));
                    });
                }
            });

            //MenuManager.getOpenedMenus().remove(player.getUniqueId());
            //MenuManager.getOpenedMenus().put(player.getUniqueId(), this);

            inventory.setContents(temporaryInventory.getContents());
            if (passed) {
                player.updateInventory();
            } else {
                player.openInventory(inventory);
                Bukkit.getConsoleSender().sendMessage(CC.translate("&cOpened new inventory"));
            }

            if (currentName != title) {
                //TODO use packets to update title
            }

            onOpenReserved(player);
            this.onOpen(player);
        } catch (Exception e) {
            Logger.error("Caught exception 1");
            e.printStackTrace();
        }
    }

    public int getInventorySize(List<Button> buttons) {
        int highest = 0;
        if (!buttons.isEmpty()) {
            highest = buttons.stream().sorted(Comparator.comparingInt(Button::getSlot).reversed()).map(Button::getSlot).collect(Collectors.toList()).get(0);
        }
        for (Button button : buttons) {
            if (button.getSlots() != null) {
                for (int i = 0; i < button.getSlots().length; i++) {
                    if (button.getSlots()[i] > highest) {
                        highest = button.getSlots()[i];
                    }
                }
            }
        }
        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    public boolean hasSlot(int value) {
        return this.buttons.stream()
                .filter(slot -> slot.getSlot() == value || slot.getSlots() != null
                        && Arrays.stream(slot.getSlots()).anyMatch(i -> i == value && shouldKeepExtra(i)))
                .findFirst().orElse(null) != null;
    }

    public Button getSlot(int value) {
        return this.buttons.stream()
                .filter(slot -> slot.getSlot() == value || slot.getSlots() != null
                        && Arrays.stream(slot.getSlots()).anyMatch(i -> i == value && shouldKeepExtra(i)))
                .findFirst().orElse(null);
    }

    public Button getBackButton(Player player) {
        return null;
    }

    public void onOpen(Player player) {

    }

    public void onOpenReserved(Player player) {
        MenuManager.getOpenedMenus().put(player.getUniqueId(), this);
    }

    public void onCloseReserved(Player player) {
        MenuManager.getLastOpenedMenus().remove(player.getUniqueId());
        MenuManager.getLastOpenedMenus().put(player.getUniqueId(), this);
        onClose(player);
    }

    public void onClose(Player player) {

    }
    public void onClose(Player player, InventoryCloseEvent event) {

    }

    public List<Button> getToolbarButtons() {
        return null;
    }

    public List<Button> getFinalExtraButtons(Player p) {
        List<Button> buttons = new ArrayList<>();
        List<Button> toolbarButtons = getToolbarButtons();
        if (toolbarButtons != null) {
            buttons.addAll(toolbarButtons);
        }
        if (getBackButton(p) != null)
            buttons.add(getBackButton(p));
        return buttons;
    }

    public boolean doesButtonExist(List<Button> buttons, int i) { //
        return buttons.stream().filter(button -> {
            if (button.getSlot() == i) {
                return true;
            }
            for (int slot : button.getSlots()) {
                if (slot == i)
                    return true;
            }
            return false;
        }).findFirst().orElse(null) != null;
    }

    public int[] genPlaceholderSpots(IntStream intStream, int... skipInput) {
        List<Integer> list = new ArrayList<>(), l1 = new ArrayList<>();
        if (skipInput != null) {
            for (int i : skipInput) {
                l1.add(i);
            }
        }
        intStream.forEach(i -> {
            if (!l1.contains(i)) {
                list.add(i);
            }
        });
        return list.stream().mapToInt(i -> i).toArray();
    }
}
