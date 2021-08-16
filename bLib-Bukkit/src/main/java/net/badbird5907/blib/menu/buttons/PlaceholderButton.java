package net.badbird5907.blib.menu.buttons;

import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.XMaterial;
import net.badbird5907.blib.version.VersionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PlaceholderButton extends Button{
    private Menu menu;
    private Player player;
    public PlaceholderButton(Menu menu,Player player){
        this.menu = menu;
        this.player = player;
    }
    public PlaceholderButton(){}
    @Override
    public ItemStack getItem(Player player) {
        if (VersionUtil.over1_13())
            return new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).name(CC.GRAY).build();
        else return new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short)7).name(CC.GRAY).build();
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public int[] getSlots() {
        List<Integer> a = new ArrayList<>(); //TODO side border.
        IntStream.range(1,9).forEach(a::add);
        a.add(43);
        IntStream.range(37,43).forEach((i)-> {
            if (i != 40) {
                /*
                if (menu != null ) {
                    if (menu.getFilterButton() != null && menu.getToolbarButtons() != null) {
                        if (menu.getFilterButton().getSlot() != i && !menu.doesButtonExist(menu.getToolbarButtons(),i))
                            a.add(i);
                    }else a.add(i); //filter button not set
                } //menu not set

                 if (buttons != null && !buttons.isEmpty()){
                    if (buttons.stream().filter(button -> {
                        if (button.getSlots() != null){
                            return button.getSlot() == i || Arrays.asList(button.getSlots()).contains(i);
                        }else{
                            return button.getSlot() == i;
                        }
                    }).findFirst().orElse(null) == null){ // button does not exist in this position
                        a.add(i);
                    }
                }
                 */
                if (menu != null){
                    if (menu.getBackButton(player) == null)
                        a.add(i);
                    if (i != 49)
                        a.add(i);
                }
                else a.add(i);
            }
        });
        return a.stream().mapToInt(i -> i).toArray();
    }

}