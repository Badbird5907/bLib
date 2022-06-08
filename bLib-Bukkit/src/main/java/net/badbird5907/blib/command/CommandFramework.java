package net.badbird5907.blib.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import static java.lang.Thread.dumpStack;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static net.badbird5907.blib.bLib.getPlugin;
import static net.badbird5907.blib.command.CommandResult.INVALID_ARGS;
import static net.badbird5907.blib.command.CommandResult.SUCCESS;
import static net.badbird5907.blib.util.Cooldown.*;
import static net.badbird5907.blib.util.ReflectionUtils.getClassesInPackage;
import static org.bukkit.Bukkit.*;
import static org.bukkit.ChatColor.*;

/**
 * Command Framework - CommandFramework <br>
 * The main command framework class used for controlling the framework.
 *
 * @author minnymin3
 */
public class CommandFramework implements CommandExecutor {
	private static CommandFramework instance;
	private final Map<String, Entry<Method, Object>> commandMap = new HashMap<>();
	private final Map<String, Command> otherMap = new HashMap<>();
	private final Plugin plugin;
	private CommandMap map;

	/**
	 * Initializes the command framework and sets up the command maps
	 *
	 * @param plugin {@link Plugin}
	 */
	public CommandFramework(Plugin plugin) {
		this.plugin = plugin;
		instance = this;
		if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
			SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();
			try {
				Field field = SimplePluginManager.class.getDeclaredField("commandMap");
				field.setAccessible(true);
				map = (CommandMap) field.get(manager);
			} catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	public static CommandFramework getInstance() {
		return instance;
	}

	private static void registerPermission(String name, String desc) {
		Permission perm = new Permission(name, desc);
		if (!getRegisteredPermissionsString().contains(name)) getPluginManager().getPermissions().add(perm);
	}

	private static Set<String> getRegisteredPermissionsString() {
		Set<String> permissions = new HashSet<>();
		getPluginManager().getPermissions().forEach(perm -> permissions.add(perm.getName()));
		return permissions;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		return handleCommand(sender, cmd, label, args);
	}

	/**
	 * Handles commands. Used in the onCommand method in your JavaPlugin class
	 *
	 * @param sender The {@link CommandSender} parsed from
	 *               onCommand
	 * @param cmd    The {@link org.bukkit.command.Command} parsed from onCommand
	 * @param label  The label parsed from onCommand
	 * @param args   The arguments parsed from onCommand
	 * @return Always returns true for simplicity's sake in onCommand
	 */
	public boolean handleCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		for (int i = args.length; i >= 0; i--) {
			String buffer = range(0, i).mapToObj(x -> "." + args[x].toLowerCase()).collect(joining("", label.toLowerCase(), ""));
			String cmdLabel = buffer.replaceFirst(plugin.getDescription().getName().toLowerCase() + ":", "").replaceFirst("/" + plugin.getDescription().getName().toLowerCase() + ":", "");
			if (commandMap.containsKey(cmdLabel)) {
				Method method = commandMap.get(cmdLabel).getKey();
				Object methodObject = commandMap.get(cmdLabel).getValue();
				Command command = method.getAnnotation(Command.class);
				if (!command.permission().equals("")) if (!sender.hasPermission(command.permission())) {
					sender.sendMessage(RED + "You do not have permission to execute this command!");
					return true;
				}
				if (command.playerOnly()) if (!(sender instanceof Player)) {
					sender.sendMessage(RED + "This command is player only!");
					return true;
				}
				//if(command.argLength() > args.length){}
				if (command.cooldown() >= 1) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						//They are currently on cooldown
						if (isOnCooldown(command.name() + "|cmd_cooldown", p.getUniqueId())) {
							p.sendMessage(RED + "You are currently on cooldown for this command! You may try again in " + GOLD + getCooldownForPlayerInt(command.name() + "|cmd_cooldown", p.getUniqueId()));
							return true;
						}
						//They used to have a cooldown
						if (wasOnCooldown(command.name() + "|cmd_cooldown", p.getUniqueId())) {
							//remove that cooldown
							removeCooldown(command.name() + "|cmd_cooldown", p.getUniqueId());
						}
						//Create a new cooldown for that
					}
				}
				try {
					CommandResult result = (CommandResult) method.invoke(methodObject, new Sender(sender), args);
					if (result == SUCCESS) return true;
					if (result == INVALID_ARGS)
						sender.sendMessage(RED + "Usage: /" + command.name() + " " + translateAlternateColorCodes('&', command.usage().replace("Usage: /" + command.name() + " ", "")));
					else if ((result != null) && !result.getMsg().equals("") && result.getMsg().equals(" "))
						sender.sendMessage(result.getMsg());
					return true;
				} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		sender.sendMessage(RED + "Unhandled command! Please report this to the plugin author! (With the stack trace that has been printed in console.)");
		dumpStack();
		return true;
	}

	/**
	 * Registers all command and completer methods inside the object. Similar
	 * to Bukkit's registerEvents method.
	 *
	 * @param obj The object to register the commands of
	 */
	public void registerCommands(Object obj) {
		stream(obj.getClass().getMethods()).forEach(m -> {
			if (m.getAnnotation(Command.class) != null) {
				Command command = m.getAnnotation(Command.class);
				if ((m.getParameterTypes().length > 2) || (m.getParameterTypes()[0] != Sender.class) || (m.getParameterTypes()[1] != String[].class)) {
					getLogger().severe("Unable to register command \"" + m.getName() + "\". Unexpected method arguments");
					return;
				}
				registerCommand(command, command.name(), m, obj);
				stream(command.aliases()).forEach(alias -> registerCommand(command, alias, m, obj));
			} else if (m.getAnnotation(Completer.class) != null) {
				Completer comp = m.getAnnotation(Completer.class);
				if ((m.getParameterTypes().length > 2) || (m.getParameterTypes().length == 0) || (m.getParameterTypes()[0] != Sender.class) || (m.getParameterTypes()[1] != String[].class)) {
					getLogger().severe("Unable to register tab completer \"" + m.getName() + "\". Unexpected method arguments");
					return;
				}
				if (m.getReturnType() != List.class) {
					getLogger().severe("Unable to register tab completer \"" + m.getName() + "\". Unexpected return type");
					return;
				}
				registerCompleter(comp.name(), m, obj);
				stream(comp.aliases()).forEach(alias -> registerCompleter(alias, m, obj));
			}
		});
	}

	/**
	 * Registers all the commands under the plugin's help
	 */
	public void registerHelp() {
		Set<HelpTopic> help = new TreeSet<HelpTopic>(HelpTopicComparator.helpTopicComparatorInstance());
		commandMap.keySet().stream().filter(s -> !s.contains(".")).map(s -> new GenericCommandHelpTopic(requireNonNull(map.getCommand(s)))).forEach(help::add);
		IndexHelpTopic topic = new IndexHelpTopic(plugin.getName(), "All commands for " + plugin.getName(), null, help, "Below is a list of all " + plugin.getName() + " commands:");
		getServer().getHelpMap().addTopic(topic);
	}

	public void registerCommand(Command command, String label, Method m, Object obj) {
		if (command.disable()) return;
		otherMap.put(label.toLowerCase(), command);
		commandMap.put(label.toLowerCase(), new AbstractMap.SimpleEntry<>(m, obj));
		commandMap.put(this.plugin.getName() + ':' + label.toLowerCase(), new AbstractMap.SimpleEntry<>(m, obj));
		String cmdLabel = label.split("\\.")[0].toLowerCase();
		if (map.getCommand(cmdLabel) == null) map.register(plugin.getName(), new BukkitCommand(cmdLabel, this, plugin));
		// make sure that we're only registering one cooldown, use command.name(); and not label because label could be an alias
		if (command.cooldown() >= 1 && !cooldownExists(command.name() + "|cmd_cooldown"))
			createCooldown(command.name() + "|cmd_cooldown");
		if (!command.description().equalsIgnoreCase("") && cmdLabel.equals(label))
			requireNonNull(map.getCommand(cmdLabel)).setDescription(command.description());
		if (!command.usage().equalsIgnoreCase("") && cmdLabel.equals(label))
			requireNonNull(map.getCommand(cmdLabel)).setUsage(command.usage());
		//if(!Bukkit.getPluginManager().getPermissions().contains())
		//	Bukkit.getPluginManager().addPermission(perm);
		if (!(command.permission() == null) && !command.permission().equals(""))
			registerPermission(command.permission(), "Permission of /" + command.name());
	}

	public void registerCompleter(String label, Method m, Object obj) {
		String cmdLabel = label.split("\\.")[0].toLowerCase();
		if (map.getCommand(cmdLabel) == null) map.register(plugin.getName(), new BukkitCommand(cmdLabel, this, plugin));
		if (map.getCommand(cmdLabel) instanceof BukkitCommand) {
			BukkitCommand command = (BukkitCommand) map.getCommand(cmdLabel);
			if (requireNonNull(command).completer == null) command.completer = new BukkitCompleter();
			command.completer.addCompleter(label, m, obj);
		} else if (map.getCommand(cmdLabel) instanceof PluginCommand) {
			try {
				Object command = map.getCommand(cmdLabel);
				Field field = requireNonNull(command).getClass().getDeclaredField("completer");
				field.setAccessible(true);
				if (field.get(command) == null) {
					BukkitCompleter completer = new BukkitCompleter();
					completer.addCompleter(label, m, obj);
					field.set(command, completer);
				} else if (field.get(command) instanceof BukkitCompleter) {
					BukkitCompleter completer = (BukkitCompleter) field.get(command);
					completer.addCompleter(label, m, obj);
				} else
					getLogger().severe("Unable to register tab completer " + m.getName() + ". A tab completer is already registered for that command!");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void registerCommandsInPackage(String packageName) {
		getClassesInPackage(getPlugin(), packageName).stream().filter(clazz -> BaseCommand.class.isAssignableFrom(clazz) && clazz.getSuperclass() == BaseCommand.class).forEach(clazz -> {
			try {
				Constructor<?> constructor = clazz.getDeclaredConstructor();
				constructor.newInstance();
			} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
				e.printStackTrace();
			}
			registerCommands(clazz);
		});
	}

	public Map<String, Command> getOtherMap() {
		return otherMap;
	}
}
