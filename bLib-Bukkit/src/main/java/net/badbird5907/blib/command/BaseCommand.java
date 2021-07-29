package net.badbird5907.blib.command;

import net.badbird5907.blib.annotation.Disable;
import net.badbird5907.blib.bLib;
import net.badbird5907.blib.util.CC;
import org.bukkit.plugin.Plugin;

import java.util.List;

public abstract class BaseCommand {
    public BaseCommand(){
        if (this.getClass().isAnnotationPresent(Disable.class))
            return;
        bLib.getCommandFramework().registerCommands(this);
    }
    public Plugin plugin = bLib.getPlugin();
    public abstract CommandResult execute(Sender sender, String[] args);
    public List<String> tabComplete(Sender sender, String[] args){
        return null;
    }
    private String usageMessage = "";

    public String getUsageMessage() {
        return usageMessage;
    }

    public void sendUsage(Sender sender){
        sender.sendMessage(CC.translate(this.getClass().getAnnotation(Command.class).usage()));
    }
}
