package de.cuzim1tigaaa.findmystation.files;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import de.cuzim1tigaaa.findmystation.data.Animation;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class CustomColors {

	private final FindMyStation plugin;
	private final Map<String, Animation> animations;

	public CustomColors(FindMyStation plugin) {
		this.plugin = plugin;
		this.animations = new HashMap<>();
		loadColors();
	}

	private void defaults(FileConfiguration configuration, File file) throws IOException {
		configuration.set("RGB.interval", 2);
		configuration.set("RGB.requirePermission", true);
		configuration.set("RGB.colors", List.of("#FF0000", "255:127:0", "255:255:0",
				"127:255:0", "#00FF00", "0:255:127", "0:255:255", "0:127:255",
				"#0000FF", "127:0:255", "255:0:255", "255:0:127"));


		configuration.setComments("RGB", List.of("The following entry is just an example and can safely be deleted ;)"));
		configuration.setComments("RGB.interval", List.of(
				"This determines, how fast the colors will switch",
				"The value indicates the ticks (1 tick = 50ms)",
				"before the next color will be selected"
		));
		configuration.setComments("RGB.requirePermission", List.of(
				"This determines, if players need the permission \"findmystation.animation.<name>\" to use it.",
				"Please notice, if this is set to true, a player also needs the permission \"findmystation.utils.animations\"",
				"This section is not needed, if the animation should be used without permission"
		));
		configuration.setComments("RGB.colors", List.of(
				"This is a list of colors in a sequence",
				"Colors can be written as hexadecimal values (as '#FFFFFF'),",
				"Minecraft color codes (as '&c') or rgb values (r:g:b, each between 0 and 255)"
		));
		configuration.save(file);
	}

	private final Pattern cc = Pattern.compile("&[0-9a-fA-F]");

	public void loadColors() {
		plugin.getLogger().log(Level.INFO, "loading animations from file...");
		try {
			File configFile = new File(plugin.getDataFolder(), "animations.yml");
			FileConfiguration config;
			if(!configFile.exists()) {
				config = new YamlConfiguration();
				defaults(config, configFile);
				getAnimations(config);
				plugin.getLogger().log(Level.INFO, animations.size() + " Animations were loaded from file");
				return;
			}
			config = YamlConfiguration.loadConfiguration(configFile);
			getAnimations(config);
			plugin.getLogger().log(Level.INFO, animations.size() + " Animations were loaded from file");
			config.save(configFile);
		}catch(IOException exception) {
			plugin.getLogger().log(Level.SEVERE, "An error occurred while getting animations", exception);
		}
	}

	private void getAnimations(FileConfiguration config) {
		animations.clear();
		for(String name : config.getKeys(false)) {
			if(this.animations.containsKey(name))
				continue;
			List<Color> colors = new ArrayList<>();
			for(String color : config.getStringList(name + ".colors")) {
				if(color.startsWith("#")) {
					try {
						ChatColor cC = ChatColor.of(color);
						colors.add(Color.fromRGB(cC.getColor().getRed(), cC.getColor().getGreen(), cC.getColor().getBlue()));
					}catch(Exception ignored) {
						plugin.getLogger().log(Level.WARNING, "Error with Animation " + name + ": " + color + " is not a valid hexadecimal color code!");
					}
					continue;
				}

				if(color.startsWith("&")) {
					Matcher matcher = cc.matcher(color);
					if(!matcher.find()) {
						plugin.getLogger().log(Level.WARNING, "Error with Animation " + name + ": " + color + " is not a valid color code!");
						continue;
					}
					ChatColor cC = ChatColor.getByChar(color.charAt(1));
					colors.add(Color.fromRGB(cC.getColor().getRed(), cC.getColor().getGreen(), cC.getColor().getBlue()));
					continue;
				}

				String[] rgbValue;

				if(color.contains(";")) rgbValue = color.split(";");
				else if(color.contains(":")) rgbValue = color.split(":");
				else {
					plugin.getLogger().log(Level.WARNING, "Error with Animation " + name + ": " + color + " is not a valid rgb color code!");
					continue;
				}

				if(rgbValue.length < 3) {
					plugin.getLogger().log(Level.WARNING, "Error with Animation " + name + ": " + color + " is not a valid rgb color code!");
					continue;
				}
				try {
					int r = Integer.parseInt(rgbValue[0]), g = Integer.parseInt(rgbValue[1]),
							b = Integer.parseInt(rgbValue[2]);
					colors.add(Color.fromRGB(r, g, b));
				}catch(NumberFormatException ignored) {
					plugin.getLogger().log(Level.WARNING, "Error with Animation " + name + ": " + color + " is not a valid rgb color code!");
				}catch(IllegalArgumentException ignored) {
					plugin.getLogger().log(Level.WARNING, "Error with Animation " + name + ": " + color + " contains value out of range 0 - 255!");
				}
			}
			if(colors.isEmpty())
				continue;
			int interval = config.getInt(name + ".interval");
			if(interval <= 0) {
				plugin.getLogger().log(Level.WARNING, "Error with Animation " + name + ": " + interval + " is not a valid interval!");
				continue;
			}
			boolean requirePermission = config.getBoolean(name + ".requirePermission", false);
			this.animations.put(name, new Animation(name, interval, requirePermission, colors));
		}
	}
}