package net.badbird5907.blib.command;

import static org.bukkit.ChatColor.RED;

public enum CommandResult {
	SUCCESS(""),
	ERROR(RED + "There was an error while processing that command!"),
	INVALID_ARGS(RED + "Invalid Arguments!"),
	PLAYER_NOT_FOUND(RED + "That player can't be found!"),
	INVALID_PLAYER(PLAYER_NOT_FOUND.getMsg()),
	ERROR_FETCHING_FROM_MOJANG(RED + "Could not find that player from the Mojang API!"),
	OTHER(""),
	MOJANG_ERROR(RED + "Could not contact the Mojang API! Please Try Again Later."),
	PLAYER_ONLY(RED + "This command is player-only!"),
	NO_PERMS(RED + "You don't have permission to execute this command!");
	private final String msg;

	CommandResult(String s) {
		this.msg = s;
	}

	public String getMsg() {
		return msg;
	}
}
