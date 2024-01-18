package de.cuzim1tigaaa.findmystation.data;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static de.cuzim1tigaaa.findmystation.data.Paths.*;

public class Config {

	private static FileConfiguration config;

	public static void sendActionBarMessage(Player player, String path, Object... replace) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getMessage(path, replace)));
	}

	private static void set(String path, List<String> comment, Object value) {
		if(value == null && config.getConfigurationSection(path) == null) config.createSection(path);
		else config.set(path, config.get(path, value));
		if(comment != null && !comment.isEmpty()) config.setComments(path, comment);
	}


	private static List<String> comments(boolean empty, String... comment) {
		List<String> comments = new ArrayList<>();
		if(empty) comments.add(null);
		if(comment != null && comment.length > 0) comments.addAll(List.of(comment));
		return comments;
	}


	public static boolean getBoolean(String path) {
		return config.getBoolean(path);
	}

	public static String getString(String path) {
		return config.getString(path);
	}

	public static int getInteger(String path) {
		return config.getInt(path);
	}

	public static String getMessage(String path, Object... replace) {
		if(!path.startsWith("Messages")) {
			return "Invalid Path";
		}

		String msg = config.getString(path);
		if(msg == null) msg = ChatColor.RED + "Error: Path " + ChatColor.GRAY + "'" + path + "' " + ChatColor.RED + "does not exist!";
		for(int i = 0; i < replace.length; i++) {
			String target = replace[i] == null ? null : (String) replace[i];
			if(target == null)
				continue;
			i++;
			String replacement = replace[i] == null ? null : replace[i].toString();
			if(config != null) msg = replacement == null ? msg : msg.replace("%" + target + "%", replacement);
		}
		return ChatColor.translateAlternateColorCodes('&', msg) ;
	}

	public static void loadConfig(FindMyStation plugin) {
		int serverVersion = Integer.parseInt(plugin.getServer().getBukkitVersion().split("\\.")[1].substring(0, 2));

		try {
			File configFile = new File(plugin.getDataFolder(), "config.yml");
			if(!configFile.exists()) {
				config = new YamlConfiguration();
				config.save(configFile);
			}
			config = YamlConfiguration.loadConfiguration(configFile);

			config.options().setHeader(comments(false,
					"This is the configuration file of the plugin. Everything should be self-explanatory",
					"If there is anything unclear, first take a look into the GitHub wiki:",
					"https://github.com/CuzIm1Tigaaa/FindMyStation/wiki"));

			set("Settings", comments(false), null);

			set(CONFIG_DURATION, comments(true,
					"This determines how long the working station",
					"should get highlighted in seconds"), 5);

			set(CONFIG_OVERRIDE, comments(true,
					"This allows players to select a different villager",
					"while another working station is still highlighted"), true);

			set(CONFIG_DEFAULT_COLOR, comments(true,
					"this is the default color of the highlighting for every player as a hexadecimal color code",
					"Players with the permission \"findmystation.command.color\" can change the color for themselves"), "#FFFFFF");

			set("Settings.RGB", comments(true), null);

			set(CONFIG_RGB_ALLOW, comments(true,
					"If this is set to true and if the player has the permission \"findmystation.command.rgb\",",
					"the highlighting will be switching through the default RGB color palette"), false);

			set(CONFIG_RGB_SPEED, comments(true,
					"This determines how fast the colors will switch in minecraft ticks (1 tick = 20ms)",
					"This only has an effect, if rgb is allowed and a player has the permission for it"), 2);

			set("Messages", comments(true), null);

			set(MESSAGE_NO_PERMISSION, comments(false),             "&cYou do not have permission to do that!");

			set(MESSAGE_NO_JOB, comments(false),                    "&cThis villager has no job");
			set(MESSAGE_NO_STATION, comments(false),                "&cThis villager has no working station");

			set(MESSAGE_COMMAND_CURRENT_COLOR, comments(false),     "&7Your current color is set to %COLOR%");
			set(MESSAGE_COMMAND_INVALID_COLOR, comments(false),     "&e%COLOR% &cis not a valid color!");
			set(MESSAGE_COMMAND_NEW_COLOR, comments(false),         "&7You set your color to %COLOR%");

			set(MESSAGE_COMMAND_RELOAD, comments(false),            "&7The plugin has been reloaded &8[&b%DURATION%ms&8]");
			set(MESSAGE_COMMAND_TOGGLE_RGB, comments(false),        "&7You have %STATUS% &7RGB-mode");
			config.save(configFile);
		}catch(IOException exception) {
			plugin.getLogger().log(Level.SEVERE, "An error occurred while loading config", exception);
		}
	}
}