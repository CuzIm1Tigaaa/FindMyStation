package de.cuzim1tigaaa.findmystation.data;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Config {

	private static FileConfiguration config;

	public static final String CONFIG_DURATION =            "Settings.Duration";
	public static final String CONFIG_OVERRIDE =            "Settings.OverrideHighlight";
	public static final String CONFIG_DEFAULT_COLOR =       "Settings.DefaultColor";

	public static final String CONFIG_RGB_ALLOW =           "Settings.RGB.Allow";
	public static final String CONFIG_RGB_SPEED =           "Settings.RGB.Speed";


	public static final String PERMISSION_COMMAND_RELOAD =  "findmystation.command.reload";
	public static final String PERMISSION_ALLOW_RGB =       "findmystation.utils.allowRGB";
	public static final String PERMISSION_RELOAD =          "findmystation.command.reload";


	public static final String MESSAGE_NO_JOB =             "Messages.Interact.NoJob";
	public static final String MESSAGE_NO_STATION =         "Messages.Interact.NoStation";

	public static final String MESSAGE_CURRENT_COLOR =      "Messages.Command.CurrentColor";
	public static final String MESSAGE_INVALID_COLOR =      "Messages.Command.InvalidColor";
	public static final String MESSAGE_NEW_COLOR =          "Messages.Command.NewColor";

	public static void sendActionBarMessage(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
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

			set("Settings", comments(true), null);

			set(CONFIG_DURATION, comments(true,
					"This determines how long the working station",
					"should get highlighted in seconds"), 5);

			set(CONFIG_OVERRIDE, comments(true,
					"This allows players to select a different villager",
					"while another working station is still highlighted"), true);

			set(CONFIG_DEFAULT_COLOR, comments(true,
					"Players with the permission ... can change the color of the highlighting", // TODO Permission
					"But this is the default color for every player as a hexadecimal color code"), "#000000");

			set("Settings.RGB", comments(true), null);

			set(CONFIG_RGB_ALLOW, comments(true,
					"Players with the permission ... can use \"RGB\" as color", // TODO Permission
					"instead of a hexadecimal color code"), false);

			set(CONFIG_RGB_SPEED, comments(true,
					"This determines how fast the colors will switch in minecraft ticks (1 tick = 20ms", // TODO Permission
					"This only has an effect, if rgb is allowed"), 2);

			config.save(configFile);
		}catch(IOException exception) {
			plugin.getLogger().log(Level.SEVERE, "An error occurred while loading config", exception);
		}
	}
}