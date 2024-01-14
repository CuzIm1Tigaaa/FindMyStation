package de.cuzim1tigaaa.findmystation.command;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import de.cuzim1tigaaa.findmystation.data.Data;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class RGBCommand implements CommandExecutor, TabCompleter {

	private final FindMyStation plugin;

	public RGBCommand(FindMyStation plugin) {
		this.plugin = plugin;
		plugin.getCommand("color").setExecutor(this);
	}

	@Override
	public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String s, @NonNull String[] args) {
		if(!(sender instanceof Player player))
			return true;

		Data.PlayerData data = plugin.getData().getData(player.getUniqueId());

		if(args.length == 0) {
			player.sendMessage(String.format("%sDeine aktuelle Farbe ist auf %s#%s %sgesetzt!",
					ChatColor.GRAY, ChatColor.of(data.getHexColor()),
					String.format("%08X", java.awt.Color.decode(data.getHexColor()).getRGB()).substring(2),
					ChatColor.GRAY));
			return true;
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
			player.sendMessage(ChatColor.RED + "Bitte gib einen gültigen Hexadezimalen Farbcode an! §8[§cz.B.: §f#FFFFFF§8]");
			return true;
		}
		data.setHexColor(newColor);
		player.sendMessage(String.format("%sDu hast deine Farbe auf %s#%s %sgesetzt!",
				ChatColor.GRAY, ChatColor.of(data.getHexColor()),
				String.format("%08X", java.awt.Color.decode(data.getHexColor()).getRGB()).substring(2),
				ChatColor.GRAY));
		return true;
	}

	@Override
	public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String s, @NonNull String[] args) {
		if(args.length == 1)
			return List.of("rgb");
		return Collections.emptyList();
	}
}