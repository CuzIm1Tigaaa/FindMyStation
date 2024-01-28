package de.cuzim1tigaaa.findmystation;

import de.cuzim1tigaaa.findmystation.data.*;
import de.cuzim1tigaaa.findmystation.events.HighlightListener;
import de.cuzim1tigaaa.findmystation.files.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class FindMyStation extends JavaPlugin {

	private Data data;
	private CustomColors customColors;

	@Override
	public void onEnable() {
		if(!checkVersion()) {
			getLogger().severe("Server version is not valid!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}else {
			getLogger().info("Server version is valid!");
		}

		reload();

		new Metrics(this);

		new FindMyStationCommand(this);
		new HighlightListener(this);
	}

	public void reload() {
		Config.loadConfig(this);
		Messages.loadLanguageFile(this);
		if(data != null)
			data.saveJson();

		data = new Data(this);
		if(customColors == null)
			customColors = new CustomColors(this);
		else
			customColors.loadColors();
	}

	private boolean checkVersion() {
		String version = getServer().getBukkitVersion().split("-")[0];
		String[] subVersion = version.split("\\.");

		if(subVersion.length > 1 && Integer.parseInt(subVersion[1]) >= 20)
			return true;

		if(subVersion.length > 1 && Integer.parseInt(subVersion[1]) == 19)
			return subVersion.length > 2 && Integer.parseInt(subVersion[2]) == 4;

		return false;
	}

	@Override
	public void onDisable() {
		if(data != null)
			data.saveJson();
	}
}