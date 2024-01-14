package de.cuzim1tigaaa.findmystation.command;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import de.cuzim1tigaaa.findmystation.data.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ColorCommand implements SubCommand {

	private final FindMyStation plugin;

	public ColorCommand(FindMyStation plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getCommand() {
		return "color";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getUsage() {
		return "color [#XXXXXX|rgb]";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof Player player))
			return;

		Data.PlayerData data = plugin.getData().getData(player.getUniqueId());

		if(args.length == 0) {
			// TODO Message
			player.sendMessage(String.format("%sDeine aktuelle Farbe ist auf %s#%s %sgesetzt!",
					ChatColor.GRAY, ChatColor.of(data.getHexColor()),
					String.format("%08X", java.awt.Color.decode(data.getHexColor()).getRGB()).substring(2),
					ChatColor.GRAY));
			return;
		}

		String newColor = args[0];
		try {
			ChatColor.of(newColor);
			data.setUseRGB(false);
		}catch(IllegalArgumentException ignored) {
			if(newColor.equalsIgnoreCase("rgb")) {
				data.setUseRGB(true);
			}

			// TODO not a valid color and not rgb
			// TODO Message
			player.sendMessage(ChatColor.RED + "Bitte gib einen gültigen Hexadezimalen Farbcode an! §8[§cz.B.: §f#FFFFFF§8]");
			return;
		}
		data.setHexColor(newColor);
		// TODO Message
		player.sendMessage(String.format("%sDu hast deine Farbe auf %s#%s %sgesetzt!",
				ChatColor.GRAY, ChatColor.of(data.getHexColor()),
				String.format("%08X", java.awt.Color.decode(data.getHexColor()).getRGB()).substring(2),
				ChatColor.GRAY));
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length == 1)
			return List.of("rgb");
		return Collections.emptyList();
	}
}