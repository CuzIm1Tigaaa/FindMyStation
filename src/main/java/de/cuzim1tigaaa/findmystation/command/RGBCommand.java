package de.cuzim1tigaaa.findmystation.command;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import de.cuzim1tigaaa.findmystation.data.*;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class RGBCommand implements SubCommand {

	private final FindMyStation plugin;

	public RGBCommand(FindMyStation plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getCommand() {
		return "rgb";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public String getUsage() {
		return "rgb [enable|disable]";
	}

	@Override
	public void execute(@NonNull CommandSender sender, @NonNull String[] args) {
		if(!(sender instanceof Player player))
			return;

		if(!sender.hasPermission(Paths.PERMISSION_COMMAND_RGB)) {
			sender.sendMessage(Config.getMessage(Paths.MESSAGE_NO_PERMISSION));
			return;
		}

		Data.PlayerData data = plugin.getData().getData(player.getUniqueId());

		if(args.length < 2) {
			data.setUseRGB(!data.isUseRGB());
			player.sendMessage(Config.getMessage(Paths.MESSAGE_COMMAND_TOGGLE_RGB, "STATUS",
					data.isUseRGB()? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
			return;
		}

		if(args[1].equalsIgnoreCase("enable")) {
			data.setUseRGB(true);
			player.sendMessage(Config.getMessage(Paths.MESSAGE_COMMAND_TOGGLE_RGB, "STATUS", ChatColor.GREEN + "enabled"));
			return;
		}
		if(args[1].equalsIgnoreCase("disable")) {
			data.setUseRGB(false);
			player.sendMessage(Config.getMessage(Paths.MESSAGE_COMMAND_TOGGLE_RGB, "STATUS", ChatColor.RED + "disabled"));
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length == 2 && sender.hasPermission(Paths.PERMISSION_COMMAND_RGB))
			return List.of("enable", "disable");
		return Collections.emptyList();
	}
}