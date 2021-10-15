package net.badbird5907.blib;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.blib.command.CommandFramework;
import net.badbird5907.blib.menu.MenuListener;
import net.badbird5907.blib.util.Glow;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.ReflectionUtils;
import net.badbird5907.blib.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@Getter
@Setter
public class bLib {
    @Getter
    @Setter
    private static bLib instance;
    @Getter
    @Setter
    private static Plugin plugin;
    @Getter
    @Setter
    private static boolean autoCompleteCommandsFromUsage = false;
    @Getter
    private static CommandFramework commandFramework;
    public bLib(Plugin plugin,String prefix){
        instance = this;
        setPlugin(plugin);
        new Logger(plugin.getLogger(),prefix,"[DEBUG]");
        Tasks.init(plugin);
        commandFramework = new CommandFramework(plugin);
        plugin.getServer().getPluginManager().registerEvents(new MenuListener(),plugin);
        Glow.init(plugin);
    }
    public bLib setAutoCompleteCommands(boolean b){
        autoCompleteCommandsFromUsage = b;
        return this;
    }
    public static bLib create(Plugin plugin){
        return new bLib(plugin,"");
    }
    public static bLib create(Plugin plugin,String prefix){
        return new bLib(plugin,prefix);
    }
    @SneakyThrows
    public void registerListenersInPackage(String p){
        for (Class<?> aClass : ReflectionUtils.getClassesInPackage(plugin, p)) {
            registerListener(aClass);
        }
    }
    @SneakyThrows
    public void registerListener(Class clazz){
        if (Listener.class.isAssignableFrom(clazz))
            Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(),plugin);
    }
}
