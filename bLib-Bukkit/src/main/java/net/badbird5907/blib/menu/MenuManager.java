package net.badbird5907.blib.menu;

import lombok.Getter;
import net.badbird5907.blib.menu.menu.Menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager {
    @Getter
    private static Map<UUID, Menu> openedMenus = new HashMap<>();
    @Getter
    private static Map<UUID, Menu> lastOpenedMenus = new HashMap<>();
}
