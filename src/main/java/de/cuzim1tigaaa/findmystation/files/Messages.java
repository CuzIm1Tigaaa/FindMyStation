package de.cuzim1tigaaa.findmystation.files;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import static de.cuzim1tigaaa.findmystation.files.Paths.*;

public final class Messages {

    private static FileConfiguration message;

    public static String getMessage(String path, Object... replace) {
        String msg = message.getString(path);
        if(msg == null) msg = ChatColor.RED + "Error: Path " + ChatColor.GRAY + "'" + path + "' " + ChatColor.RED + "does not exist!";
        for(int i = 0; i < replace.length; i++) {
            String target = replace[i] == null ? null : (String) replace[i];
            if(target == null)
                continue;
            i++;
            String replacement = replace[i] == null ? null : replace[i].toString();
            if(message != null) msg = replacement == null ? msg : msg.replace("%" + target + "%", replacement);
        }
        return ChatColor.translateAlternateColorCodes('&', msg) ;
    }

    public static void loadLanguageFile(FindMyStation plugin) {
        createLangFiles(plugin);
        File messageFile = new File(plugin.getDataFolder() + "/lang", Config.getString(Paths.CONFIG_LANGUAGE) + ".yml");
        try {
            if(!messageFile.exists()) {
                loadDefaultMessages(plugin);
                return;
            }
            message = YamlConfiguration.loadConfiguration(messageFile);
            message.save(messageFile);
        }catch(IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading language files", exception);
        }
    }

    private static void createLangFiles(FindMyStation plugin) {
        URL dirURL = FindMyStation.class.getClassLoader().getResource("lang");
        if(dirURL == null) return;

        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));

        try(JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
	        Enumeration<JarEntry> entries = jarFile.entries();

            while(entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if(name.startsWith("lang"))
                    if(!name.endsWith(File.separator) && name.endsWith(".yml")) {
                        File file = new File(plugin.getDataFolder(), name);
                        if(!file.exists()) plugin.saveResource(name, false);
                    }
            }
        }catch(IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while creating language files", exception);
        }
    }

    private static void loadDefaultMessages(FindMyStation plugin) {
        File messageFile = new File(plugin.getDataFolder() + "/lang", "en_US.yml");
        try {
            if(!messageFile.exists()) {
                message = new YamlConfiguration();
                message.save(messageFile);
            }
            message = YamlConfiguration.loadConfiguration(messageFile);
            set(MESSAGE_NO_JOB,                    "&cThis villager has no job");
            set(MESSAGE_NO_STATION,                "&cThis villager has no working station");

            set(MESSAGE_NO_PERMISSION,             "&cYou do not have permission to do that!");

            set(MESSAGE_COMMAND_COLOR_CURRENT,     "&7Working stations are currently highlighted in %COLOR%");
            set(MESSAGE_COMMAND_COLOR_INVALID,     "&e%COLOR% &cis not a valid color!");
            set(MESSAGE_COMMAND_COLOR_NEW,         "&7Working stations will now be highlighted in %COLOR%");

            set(MESSAGE_COMMAND_ANIMATION_CURRENT, "&7Working stations are currently highlighted with animation &b%ANIMATION%");
            set(MESSAGE_COMMAND_ANIMATION_INVALID, "&e%ANIMATION% &cis not a valid animation!");
            set(MESSAGE_COMMAND_ANIMATION_NEW,     "&7Working stations will now be highlighted with animation &b%ANIMATION%");
            set(MESSAGE_COMMAND_ANIMATION_NONE,    "&cThere is no animation that you can select!");
            set(MESSAGE_COMMAND_ANIMATION_LIST,    "&7List of available Animations:");
            set(MESSAGE_COMMAND_ANIMATION_ENABLE,  "&7Working stations are &anow &7highlighted with an animation!");
            set(MESSAGE_COMMAND_ANIMATION_DISABLE, "&7Working stations are &cno longer &7highlighted with an animation!");

            set(MESSAGE_COMMAND_RELOAD,            "&7The plugin has been reloaded &8[&b%DURATION%ms&8]");
            message.save(messageFile);
        }catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading default messages", exception);
        }
    }

    private static void set(String path, String value) {
        message.set(path, message.get(path, value));
    }
}