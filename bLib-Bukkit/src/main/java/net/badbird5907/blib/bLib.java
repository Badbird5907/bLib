package net.badbird5907.blib;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.command.CommandFramework;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
import org.bukkit.plugin.Plugin;

@Getter
@Setter
public class bLib {
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
	}
}
