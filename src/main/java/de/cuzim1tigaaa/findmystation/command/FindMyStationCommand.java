package de.cuzim1tigaaa.findmystation.command;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import org.bukkit.command.*;

import java.util.Collections;
import java.util.List;

public class FindMyStationCommand implements CommandExecutor, TabCompleter {

	private final FindMyStation plugin;

	public FindMyStationCommand(FindMyStation plugin) {
		this.plugin = plugin;
		plugin.getCommand("findmystation").setExecutor(this);
	}


	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
		return Collections.emptyList();
	}
}