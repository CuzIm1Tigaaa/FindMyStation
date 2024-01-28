package de.cuzim1tigaaa.findmystation.command;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import de.cuzim1tigaaa.findmystation.SubCommand;
import de.cuzim1tigaaa.findmystation.files.*;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class SubReload implements SubCommand {

	private final FindMyStation plugin;

	public SubReload(FindMyStation plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getCommand() {
		return "reload";
	}

	@Override
	public String getPermission() {
		return Paths.PERM_COMMAND_RELOAD;
	}

	@Override
	public List<String> getAliases() {
		return List.of("rl");
	}

	@Override
	public String getUsage() {
		return "reload";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission(Paths.PERM_COMMAND_RELOAD)) {
			sender.sendMessage(Messages.getMessage(Paths.MESSAGE_NO_PERMISSION));
			return;
		}
		long started = System.currentTimeMillis();
		plugin.reload();
		sender.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_RELOAD, "DURATION", System.currentTimeMillis() - started));
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}
}