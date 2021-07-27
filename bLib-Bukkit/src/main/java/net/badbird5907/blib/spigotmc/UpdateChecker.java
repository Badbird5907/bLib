package net.badbird5907.blib.spigotmc;

import net.badbird5907.blib.bLib;
import net.badbird5907.blib.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    private int resourceId;

    public UpdateChecker(int resourceId) {
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        if (resourceId == -1){
            return;
        }
        Tasks.runAsync(()->{
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                bLib.getPlugin().getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}
