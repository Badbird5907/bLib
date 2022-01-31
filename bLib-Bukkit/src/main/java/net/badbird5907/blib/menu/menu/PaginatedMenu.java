package net.badbird5907.blib.menu.menu;

import lombok.Getter;
import net.badbird5907.blib.menu.MenuManager;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.buttons.impl.NextPageButton;
import net.badbird5907.blib.menu.buttons.impl.PreviousPageButton;
import net.badbird5907.blib.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public abstract class PaginatedMenu extends Menu {

    @Getter
    private int page = 1;
    @Override
   public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Button> toolbar = getToolbarButtons();
        if (toolbar == null) {
            toolbar = new ArrayList<>();
        }
        List<Button> bottom = new ArrayList<>();
        //int minSlot = (int) ((double) (page - 1) * 27);
        //int maxSlot = (int) ((double) (page) * 27);
        int minSlot = (int) ((double) (page - 1) * getMaxPageItems());
        int maxSlot = (int) ((double) (page) * getMaxPageItems());
        Button next = new NextPageButton(this),previous = new PreviousPageButton(this);
        buttons.add(next);
        buttons.add(previous);
        bottom.add(next);
        bottom.add(previous);
        Button backButton = getBackButton(player);
        if (backButton != null){
            bottom.add(backButton);
            buttons.add(backButton);
        }
        if (toolbar != null){
            buttons.addAll(toolbar);
        }
        Button close = getCloseButton(),filter = getFilterButton();
        if (close != null) {
            buttons.add(close);
            bottom.add(close);
        }
        if (filter != null){
            buttons.add(filter);
            bottom.add(filter);
        }
        List<Button> overrideEveryMenu = getEveryMenuSlots(player);
        if (overrideEveryMenu != null) {
            overrideEveryMenu.forEach(slot -> buttons.removeIf(s -> s.hasSlot(slot.getSlot()))); //remove any old buttons that conflict with the new ones
            buttons.addAll(overrideEveryMenu);
        }
        AtomicInteger index = new AtomicInteger(0);
        this.getPaginatedButtons(player).forEach(button -> { // loop through all paginated buttons
            int current = index.getAndIncrement(); // current index
            if (current >= minSlot && current < maxSlot) {
                current -= (int) ((double) (getMaxPageItems()) * (page - 1)) - 9;
                buttons.add(this.getNewButton(button, current)); // add new button overriding the slot
            }
        });
        Button placeholder = getPlaceholderButton();
        if (placeholder != null)
            buttons.add(placeholder);
        else buttons.add(new PaginatedPlaceholderButton(toolbar,bottom));
        return buttons;
    }


    private Button getNewButton(Button button, int s) {
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
            public void onClick(Player player, int s, ClickType clickType, InventoryClickEvent event) {
                button.onClick(player, s, clickType, event);
            }

            @Override
            public boolean hasSlot(int slot) {
                return button.hasSlot(slot);
            }
        };
    }

    @Override
    public String getName(Player player) {
        if (showPageNumbersInTitle()){
            return this.getPagesTitle(player) + CC.R + " (" + page + "/" + getPages(player) + ")";
        }else return this.getPagesTitle(player);
    }

    public void changePage(Player player, int page) {
        this.page += page;
        this.getButtons().clear();
        this.update(player);
    }

    public int getPages(Player player) {
        List<Button> buttons = this.getPaginatedButtons(player);
        if (buttons.isEmpty()) {
            return 1;
        }
        //return (int) Math.ceil(getPaginatedButtons(player).size() / (double) 27);
        return (int) Math.ceil(buttons.size() / (double) getMaxPageItems());
    }

    public abstract String getPagesTitle(Player player);

    public abstract List<Button> getPaginatedButtons(Player player);

    public List<Button> getEveryMenuSlots(Player player){
        return null;
    }
    public Button getFilterButton(){
        return null;
    }
    public Button getPlaceholderButton(){
        return null;
    }
    public boolean showPageNumbersInTitle(){
        return true;
    }
    public int getMaxPageItems(){
        return 27;
    }
    public int getMenuSize(){
        return getMaxPageItems() + 18; //if it dosent work, set to 17
    }
    public boolean doesButtonExist(List<Button> buttons,int i){
        return buttons.stream().filter(button ->{
            if (button.getSlot() == i){
                return true;
            }
            for (int slot : button.getSlots()) {
                if (slot == i)
                    return true;
            }
            return false;
        }).findFirst().orElse(null) != null;
    }

    @Override
    public final void onOpenReserved(Player player) {
    }

    @Override
    public final void onCloseReserved(Player player) {
        MenuManager.getOpenedMenus().remove(player.getUniqueId());
    }

    public class PaginatedPlaceholderButton extends PlaceholderButton {
        private final List<Integer> topslots = new ArrayList<>();
        private final List<Integer> bottomslots = new ArrayList<>();

        public PaginatedPlaceholderButton(List<Button> top, List<Button> bottom) {
            for (Button button : top) {
                topslots.add(button.getSlot());
                if (button.getSlots() != null) {
                    for (int slot : button.getSlots()) {
                        topslots.add(slot);
                    }
                }
            }
            for (Button button : bottom) {
                bottomslots.add(button.getSlot());
                if (button.getSlots() != null) {
                    for (int slot : button.getSlots()) {
                        bottomslots.add(slot);
                    }
                }
            }
            //Logger.debug(topslots);
            //Logger.debug(bottomslots);
        }

        @Override
        public int getSlot() {
            return -1;
        }

        @Override
        public int[] getSlots() {
            Set<Integer> a = new HashSet<>(); //TODO side border.
            IntStream.range(0,9).forEach(i ->{ //top toolbar, skip slots that are used
                if (!topslots.contains(i))
                    a.add(i);
            });
            IntStream.range(getMenuSize() - 9, getMenuSize()).forEach(i -> { //bottom toolbar, skip slots that are used
                if (!bottomslots.contains(i))
                    a.add(i);
            });
            int[] slots = a.stream().mapToInt(i -> i).toArray();
            //Logger.debug(Arrays.toString(slots));
            return slots;
        }
    }
}
