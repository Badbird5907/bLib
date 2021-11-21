package net.badbird5907.blib.menu.menu;

import lombok.Getter;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.buttons.impl.NextPageButton;
import net.badbird5907.blib.menu.buttons.impl.PreviousPageButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.ceil;
import static java.util.Arrays.stream;

public abstract class PaginatedMenu extends Menu {

	@Getter
	private int page = 1;

	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();
		int minSlot = (int) ((double) (page - 1) * 27);
		int maxSlot = (int) ((double) (page) * 27);
		buttons.add(new NextPageButton(this));
		buttons.add(new PreviousPageButton(this));
		if (getToolbarButtons() != null) buttons.addAll(getToolbarButtons());
		buttons.add((getPlaceholderButton() != null) ? getPlaceholderButton() : new PlaceholderButton(this, player));
		buttons.add(getCloseButton());
		if (getFilterButton() != null) buttons.add(getFilterButton());
		if (this.getEveryMenuSlots(player) != null) {
			this.getEveryMenuSlots(player).forEach(slot -> buttons.removeIf(s -> s.hasSlot(slot.getSlot())));
			buttons.addAll(this.getEveryMenuSlots(player));
		}
		AtomicInteger index = new AtomicInteger(0);
		this.getPaginatedButtons(player).forEach(slot -> {
			int current = index.getAndIncrement();
			if (current >= minSlot && current < maxSlot) {
				current -= (int) ((double) (27) * (page - 1)) - 9;
				buttons.add(this.getNewSlot(slot, current));
			}
		});
		return buttons;
	}

	private Button getNewSlot(Button button, int s) {
		return new Button() {
			@Override
			public ItemStack getItem(Player player) {
				return button.getItem(player);
			}

			@Override
			public int getSlot() {
				return s;
			}

			@Override
			public void onClick(Player player, int s, ClickType clickType) {
				button.onClick(player, s, clickType);
			}
		};
	}

	@Override
	public String getName(Player player) {
		return this.getPagesTitle(player);
	}

	public void changePage(Player player, int page) {
		this.page += page;
		this.getButtons().clear();
		this.update(player);
	}

	public int getPages(Player player) {
		return this.getPaginatedButtons(player).isEmpty() ? 1 : (int) ceil(getPaginatedButtons(player).size() / 27.0);
	}

	public abstract String getPagesTitle(Player player);

	public abstract List<Button> getPaginatedButtons(Player player);

	public abstract List<Button> getEveryMenuSlots(Player player);

	public Button getFilterButton() {
		return null;
	}

	public Button getPlaceholderButton() {
		return null;
	}

	public boolean doesButtonExist(List<Button> buttons, int i) {
		return buttons.stream().filter(button -> {
			if (button.getSlot() == i) return true;
			return stream(button.getSlots()).anyMatch(slot -> slot == i);
		}).findFirst().orElse(null) != null;
	}
}
