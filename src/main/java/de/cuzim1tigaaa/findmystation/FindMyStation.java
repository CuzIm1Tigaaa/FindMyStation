package de.cuzim1tigaaa.findmystation;

import de.cuzim1tigaaa.findmystation.command.FindMyStationCommand;
import de.cuzim1tigaaa.findmystation.data.Config;
import de.cuzim1tigaaa.findmystation.data.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class FindMyStation extends JavaPlugin {

	private Data data;

	@Override
	public void onEnable() {
		if(!checkVersion()) {
			getLogger().severe("Server version is not valid!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}else {
			getLogger().info("Server version is valid!");
		}

		Config.loadConfig(this);
		data = new Data(this);

		new FindMyStationCommand(this);
		new HighlightListener(this);
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