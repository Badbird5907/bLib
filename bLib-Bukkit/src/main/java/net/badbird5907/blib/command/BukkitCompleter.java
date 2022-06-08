package net.badbird5907.blib.command;

import net.badbird5907.blib.bLib;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Command Framework - BukkitCompleter <br>
 * An implementation of the TabCompleter class allowing for multiple tab
 * completers per command
 *
 * @author minnymin3
 */
public class BukkitCompleter implements TabCompleter {
	private Map<String, Entry<Method, Object>> completers = new HashMap<>();

	public void addCompleter(String label, Method m, Object obj) {
		completers.put(label, new AbstractMap.SimpleEntry<>(m, obj));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		for (int i = args.length; i >= 0; i--) {
			String cmdLabel = IntStream.range(0, i).filter(x -> !args[x].equals("") && !args[x].equals(" ")).mapToObj(x -> "." + args[x].toLowerCase()).collect(Collectors.joining("", label.toLowerCase(), ""));
			if (completers.containsKey(cmdLabel)) {
				Entry<Method, Object> entry = completers.get(cmdLabel);
				try {
					//return (List<String>) entry.getKey().invoke(entry.getValue(), new CommandArgs(sender, command, label, args, cmdLabel.split("\\.").length - 1));
					return (List<String>) entry.getKey().invoke(entry.getValue(), new Sender(sender), args);
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			} else if (bLib.isAutoCompleteCommandsFromUsage() && CommandFramework.getInstance().getOtherMap().containsKey(cmdLabel.toLowerCase())) {
				net.badbird5907.blib.command.Command command1 = CommandFramework.getInstance().getOtherMap().get(cmdLabel.toLowerCase());
				if (!command1.usage().isEmpty()) {
					String[] options = command1.usage().split("\\s+");
					int already = args.length;
					List<String> list = new ArrayList<>();
					int index = 0;
					for (String option : options) {
						index++;
						if (index < already) continue;
						if (option.startsWith("<") && option.endsWith(">"))
							Arrays.stream(option.replace("<", "").replace(">", "").split("/")).forEach(s -> {
								if (s.equalsIgnoreCase("player"))
									Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).forEach(list::add);
								else list.add(s);
							});
						else if (option.startsWith("[") && option.endsWith("]")) {
							Arrays.stream(option.replace("[", "").replace("]", "").split("/")).forEach(s -> {
								if (s.equalsIgnoreCase("player"))
									Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).forEach(list::add);
								else list.add(s);
							});
						}
					}
					return list;
				}
			}
		}
		return null;
	}
}
