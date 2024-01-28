package de.cuzim1tigaaa.findmystation.files;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static de.cuzim1tigaaa.findmystation.files.Paths.*;

public class Config {

	private static FileConfiguration config;

	public static void sendActionBarMessage(Player player, String path, Object... replace) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage(path, replace)));
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

			set("Settings", Collections.emptyList(), null);

			set(CONFIG_LANGUAGE, comments(true,
					"Specify which language file should be used by the plugin",
					"You can also add new languages! :)"), "en_US");

			set(CONFIG_DURATION, comments(true,
					"This determines how long the working station will be highlighted",
					"This value indicates the seconds"), 5);

			set(CONFIG_OVERRIDE, comments(true,
					"This allows players to select a different villager",
					"while another working station is still highlighted"), true);

			set(CONFIG_DEFAULT_COLOR, comments(true,
					"this is the default color of the highlighting for every player as a hexadecimal color code",
					"Players with the permission \"findmystation.commands.color\" can change the color for themselves"), "#FFFFFF");

			set(CONFIG_ALLOW_ANIMATIONS, comments(true,
					"If this is set to true, the highlighting will animated. The animations can be configured (view animations.yml)",
					"A player also needs the permission \"findmystation.animations.<name>\" and \"findmystation.utils.animations\"",
					"or just \"findmystation.animations.*\" for this to have an effect"), false);

			config.save(configFile);
		}catch(IOException exception) {
			plugin.getLogger().log(Level.SEVERE, "An error occurred while loading config", exception);
		}
	}
}