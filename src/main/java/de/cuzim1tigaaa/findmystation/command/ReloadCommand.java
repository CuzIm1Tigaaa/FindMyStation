package de.cuzim1tigaaa.findmystation.command;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import de.cuzim1tigaaa.findmystation.data.Config;
import de.cuzim1tigaaa.findmystation.data.Paths;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand implements SubCommand {

	private final FindMyStation plugin;

	public ReloadCommand(FindMyStation plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getCommand() {
		return "reload";
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
		if(!sender.hasPermission(Paths.PERMISSION_COMMAND_RELOAD)) {
			sender.sendMessage(Config.getMessage(Paths.MESSAGE_NO_PERMISSION));
			return;
		}
		long started = System.currentTimeMillis();
		Config.loadConfig(plugin);
		sender.sendMessage(Config.getMessage(Paths.MESSAGE_COMMAND_RELOAD, "DURATION", System.currentTimeMillis() - started));
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}
}