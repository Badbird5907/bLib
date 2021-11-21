package net.badbird5907.blib.menu.menu;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.impl.CloseButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.ceil;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static net.badbird5907.blib.bLib.getPlugin;
import static net.badbird5907.blib.menu.MenuManager.getLastOpenedMenus;
import static net.badbird5907.blib.menu.MenuManager.getOpenedMenus;
import static net.badbird5907.blib.util.CC.translate;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Bukkit.getConsoleSender;


public abstract class Menu {
	public Plugin plugin = getPlugin();
	@Getter
	@Setter
	public Menu previous;
	@Getter
	private List<Button> buttons = new ArrayList<>();
	@Setter
	@Getter
	private boolean updateInTask = false;
	@Getter
	@Setter
	private boolean cancel = true;

	public abstract List<Button> getButtons(Player player);

	public abstract String getName(Player player);

	public List<Button> getFinalButtons(Player player) {
		List<Button> list = getButtons(player);
		if (list == null) list = new ArrayList<>();
		Button backButton = getBackButton(player);
		if (backButton != null) list.add(backButton);
		return list;
	}

	public void open(Sender sender) {
		open(sender.getPlayer());
	}

	public void open(Player player) {
		Menu previous = getOpenedMenus().get(player.getUniqueId());
		if (previous != null) {
			setPrevious(previous);
			previous.onClose(player);
			getOpenedMenus().remove(player.getUniqueId());
		}
		this.buttons = this.getFinalButtons(player);
		String title = this.getName(player);
		if (title.length() > 32) title = title.substring(0, 32);
		title = translate(title);
		if (player.getOpenInventory() != null) player.closeInventory();

		Inventory inventory = createInventory(player, this.getInventorySize(this.buttons), title);
		this.buttons.forEach(button -> {
			inventory.setItem(button.getSlot(), button.getItem(player));
			if (button.getSlots() != null)
				stream(button.getSlots()).filter(this::shouldKeepExtra).forEach(extra -> inventory.setItem(extra, button.getItem(player)));
		});
		getOpenedMenus().put(player.getUniqueId(), this);
		player.openInventory(inventory);
		this.onOpen(player);
	}

	private boolean shouldKeepExtra(int slot) {
		for (Button button1 : this.buttons)
			if (button1.getSlot() == slot) return false;
			else if (newArrayList(button1.getSlots()).contains(slot)) return true;
		return true;
	}

	public Button getCloseButton() {
		return new CloseButton();
	}

	public void update(Player player) {
		this.buttons = this.getFinalButtons(player);
		String title = this.getName(player);
		if (title.length() > 32) title = title.substring(0, 32);
		title = translate(title);
		boolean passed = false;
		Inventory inventory = null;
		Menu currentlyOpenedMenu = getOpenedMenus().get(player.getUniqueId());
		Inventory current = player.getOpenInventory().getTopInventory();
		if ((currentlyOpenedMenu != null) && translate(currentlyOpenedMenu.getName(player)).equals(player.getOpenInventory().getTitle()) && (current.getSize() == this.getInventorySize(this.buttons))) {
			inventory = current;
			passed = true;
		}
		if (inventory == null) inventory = createInventory(player, this.getInventorySize(this.buttons), title);
		/*
		 * TemporaryInventory
		 * Used to prevent item flickering because 'inventory' is live player inventory
		 */
		Inventory temporaryInventory = createInventory(player, inventory.getSize(), player.getOpenInventory().getTitle());
		this.buttons.forEach(slot -> {
			temporaryInventory.setItem(slot.getSlot(), slot.getItem(player));
			if (slot.getSlots() != null)
				stream(slot.getSlots()).forEach(extra -> temporaryInventory.setItem(extra, slot.getItem(player)));
		});
		getOpenedMenus().remove(player.getUniqueId());
		getOpenedMenus().put(player.getUniqueId(), this);
		inventory.setContents(temporaryInventory.getContents());
		if (passed) player.updateInventory();
		else {
			player.openInventory(inventory);
			getConsoleSender().sendMessage(translate("&cOpened new inventory"));
		}
		this.onOpen(player);
	}

	public int getInventorySize(List<Button> buttons) {
		int highest = 0;
		if (!buttons.isEmpty())
			highest = buttons.stream().sorted(Comparator.comparingInt(Button::getSlot).reversed()).map(Button::getSlot).collect(toList()).get(0);
		for (Button button : buttons)
			if (button.getSlots() != null) for (int i = 0; i < button.getSlots().length; i++)
				if (button.getSlots()[i] > highest) highest = button.getSlots()[i];
		return (int) (ceil((highest + 1) / 9D) * 9D);
	}

	public boolean hasSlot(int value) {
		return this.buttons.stream().filter(slot -> (slot.getSlot() == value) || ((slot.getSlots() != null) && stream(slot.getSlots()).anyMatch(i -> (i == value) && shouldKeepExtra(i)))).findFirst().orElse(null) != null;
	}

	public Button getSlot(int value) {
		return this.buttons.stream().filter(slot -> (slot.getSlot() == value) || ((slot.getSlots() != null) && stream(slot.getSlots()).anyMatch(i -> (i == value) && shouldKeepExtra(i)))).findFirst().orElse(null);
	}

	public Button getBackButton(Player player) {
		return null;
	}

	public void onOpen(Player player) {
	}

	public void onClose(Player player) {
		getLastOpenedMenus().remove(player.getUniqueId());
		getLastOpenedMenus().put(player.getUniqueId(), this);
	}

	public List<Button> getToolbarButtons() {
		return null;
	}

	public List<Button> getFinalExtraButtons(Player p) {
		List<Button> buttons = new ArrayList<>();
		if (getToolbarButtons() != null) buttons.addAll(getToolbarButtons());
		if (getBackButton(p) != null) buttons.add(getBackButton(p));
		return buttons;
	}

	public boolean doesButtonExist(List<Button> buttons, int i) { //
		return buttons.stream().filter(button -> {
			if (button.getSlot() == i) return true;
			return stream(button.getSlots()).anyMatch(slot -> slot == i);
		}).findFirst().orElse(null) != null;
	}

	public int[] genPlaceholderSpots(IntStream intStream, int... skipInput) {
		List<Integer> list, l1 = new ArrayList<>();
		if (skipInput != null) l1 = stream(skipInput).boxed().collect(toList());
		List<Integer> finalL = l1;
		list = intStream.filter(i -> !finalL.contains(i)).boxed().collect(toList());
		return list.stream().mapToInt(i -> i).toArray();
	}
}
