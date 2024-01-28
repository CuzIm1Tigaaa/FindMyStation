package de.cuzim1tigaaa.findmystation;

import de.cuzim1tigaaa.findmystation.command.*;
import de.cuzim1tigaaa.findmystation.files.Messages;
import de.cuzim1tigaaa.findmystation.files.Paths;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

import java.util.*;
import java.util.stream.Collectors;

public class FindMyStationCommand implements CommandExecutor, TabCompleter {

	private final List<SubCommand> subCommands;

	public FindMyStationCommand(FindMyStation plugin) {
		this.subCommands = new ArrayList<>();
		plugin.getCommand("findmystation").setExecutor(this);

		this.subCommands.add(new SubAnimation(plugin));
		this.subCommands.add(new SubColor(plugin));
		this.subCommands.add(new SubReload(plugin));
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if(args.length == 0) {
			subCommands.forEach(sub -> sender.sendMessage(ChatColor.GRAY + " » " + ChatColor.RED + "/findmystation " + sub.getUsage()));
			return true;
		}

		SubCommand selected = subCommands.stream()
				.filter(sub -> args[0].equalsIgnoreCase(sub.getCommand()) ||
						(!sub.getAliases().isEmpty() && sub.getAliases().contains(args[0].toLowerCase())))
				.findFirst().orElse(null);

		if(selected == null) {
			subCommands.forEach(sub -> sender.sendMessage(ChatColor.GRAY + " » " + ChatColor.RED + "/findmystation " + sub.getUsage()));
			return true;
		}
		if(!sender.hasPermission(selected.getPermission())) {
			sender.sendMessage(Messages.getMessage(Paths.MESSAGE_NO_PERMISSION));
			return true;
		}

		selected.execute(sender, args);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
		if(args.length == 0)
			return Collections.emptyList();

		if(args.length == 1)
			return subCommands.stream().filter(sub -> sender.hasPermission(sub.getPermission())).map(SubCommand::getCommand).collect(Collectors.toList());

		SubCommand selected = subCommands.stream()
				.filter(sub -> args[0].equalsIgnoreCase(sub.getCommand()) ||
						(!sub.getAliases().isEmpty() && sub.getAliases().contains(args[0].toLowerCase())))
				.findFirst().orElse(null);

		if(selected == null)
			return Collections.emptyList();
		return selected.tabComplete(sender, args);
	}
}