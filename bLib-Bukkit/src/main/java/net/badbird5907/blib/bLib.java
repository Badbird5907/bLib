package net.badbird5907.blib;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.blib.command.CommandFramework;
import net.badbird5907.blib.menu.MenuListener;
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
    private static CommandFramework commandFramework;
    public bLib(Plugin plugin,String prefix){
        instance = this;
        setPlugin(plugin);
        new Logger(plugin.getLogger(),prefix,"[DEBUG]");
        new Tasks(plugin);
        commandFramework = new CommandFramework(plugin);
        registerListener(MenuListener.class);
    }
    @SneakyThrows
    public void registerListenersInPackage(String p){
        for (Class<?> aClass : ReflectionUtils.getClassesInPackage(plugin, p)) {
            registerListener(aClass);
        }
    }
    @SneakyThrows
    public void registerListener(Class clazz){
        if (clazz.isAssignableFrom(Listener.class))
            Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(),plugin);
    }
}
