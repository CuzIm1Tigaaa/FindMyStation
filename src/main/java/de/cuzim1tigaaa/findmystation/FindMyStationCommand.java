package de.cuzim1tigaaa.findmystation;

import de.cuzim1tigaaa.findmystation.command.*;
import org.bukkit.command.*;

import java.util.*;
import java.util.stream.Collectors;

public class FindMyStationCommand implements CommandExecutor, TabCompleter {

	private final List<SubCommand> subCommands;

	public FindMyStationCommand(FindMyStation plugin) {
		this.subCommands = new ArrayList<>();
		plugin.getCommand("findmystation").setExecutor(this);
		this.subCommands.add(new ColorCommand(plugin));
		this.subCommands.add(new ReloadCommand(plugin));
		this.subCommands.add(new RGBCommand(plugin));
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if(args.length == 0) {

			return true;
		}

		subCommands.stream().filter(sc -> args[0].equalsIgnoreCase(sc.getCommand()) ||
						(sc.getAliases() != null && !sc.getAliases().isEmpty() && sc.getAliases().contains(args[0].toLowerCase())))
				.findFirst().ifPresent(sc -> sc.execute(sender, args));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
		if(args.length == 0)
			return Collections.emptyList();

		if(args.length == 1)
			return subCommands.stream().map(SubCommand::getCommand).collect(Collectors.toList());

		SubCommand command =  subCommands.stream().filter(sc -> args[0].equalsIgnoreCase(sc.getCommand()) ||
						(sc.getAliases() != null && !sc.getAliases().isEmpty() && sc.getAliases().contains(args[0].toLowerCase())))
				.findFirst().orElse(null);
		if(command == null)
			return Collections.emptyList();
		return command.tabComplete(sender, args);
	}
}