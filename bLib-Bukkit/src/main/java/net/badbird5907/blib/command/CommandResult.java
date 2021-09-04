package net.badbird5907.blib.command;

import org.bukkit.ChatColor;

public enum CommandResult {
    SUCCESS(""),
    ERROR(ChatColor.RED + "There was an error while processing that command!"),
    INVALID_ARGS(ChatColor.RED + "Invalid Arguments!"),
    PLAYER_NOT_FOUND(ChatColor.RED + "That player can't be found!"),
    INVALID_PLAYER(PLAYER_NOT_FOUND.getMsg()),
    ERROR_FETCHING_FROM_MOJANG(ChatColor.RED + "Could not find that player from the Mojang API!"),
    OTHER(""),
    MOJANG_ERROR(ChatColor.RED + "Could not contact the Mojang API! Please Try Again Later."),
    PLAYER_ONLY(ChatColor.RED + "This command is player-only!"),
    NO_PERMS(ChatColor.RED + "You don't have permission to execute this command!");
    private String msg;
    CommandResult(String s){
        this.msg = s;
    }

    public String getMsg() {
        return msg;
    }
}
