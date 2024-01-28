package de.cuzim1tigaaa.findmystation;

import lombok.NonNull;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

	String getCommand();
	String getPermission();

	@NonNull
	List<String> getAliases();
	String getUsage();

	void execute(CommandSender sender, String[] args);
	List<String> tabComplete(CommandSender sender, String[] args);
}