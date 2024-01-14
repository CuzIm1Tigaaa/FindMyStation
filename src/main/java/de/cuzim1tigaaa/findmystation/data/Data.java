package de.cuzim1tigaaa.findmystation.data;

import com.google.gson.Gson;
import de.cuzim1tigaaa.findmystation.FindMyStation;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class Data {

	private final FindMyStation plugin;
	private final Set<PlayerData> dataSet;

	private final File dataFile;

	@Getter
	public static class PlayerData {
		private final UUID uuid;
		@Setter private boolean useRGB;
		@Setter private String hexColor;

		public PlayerData(UUID uuid) {
			this.uuid = uuid;
			this.useRGB = Config.getBoolean(Config.CONFIG_RGB_ALLOW);
			this.hexColor = Config.getString(Config.CONFIG_DEFAULT_COLOR);
		}

		public PlayerData(UUID uuid, boolean useRGB, String hexColor) {
			this.uuid = uuid;
			this.useRGB = useRGB;
			this.hexColor = hexColor;
		}
	}


	public PlayerData getData(UUID uuid) {
		PlayerData pData = dataSet.stream().filter(dat -> dat.getUuid().equals(uuid)).findFirst().orElse(null);
		if(pData == null) {
			pData = new PlayerData(uuid);
			dataSet.add(pData);
		}
		return pData;
	}


	public Data(FindMyStation plugin) {
		this.plugin = plugin;
		this.dataFile = new File(plugin.getDataFolder(), "playerdata.json");

		this.dataSet = new HashSet<>();
		this.loadJson();
	}

	private void loadJson() {
		if(!dataFile.exists())
			return;

		try(FileReader fileReader = new FileReader(dataFile)) {
			PlayerData[] data = new Gson().fromJson(fileReader, PlayerData[].class);
			this.dataSet.addAll(Set.of(data));
		}catch(IOException exception) {
			plugin.getLogger().log(Level.WARNING, "An error occurred while loading data!", exception);
		}
	}

	public void saveJson() {
		if(this.dataSet == null || this.dataSet.isEmpty())
			return;

		Gson gson = new Gson();
		if(!dataFile.exists()) {
			try {
				dataFile.getParentFile().mkdir();
				dataFile.createNewFile();
			}catch(IOException exception) {
				plugin.getLogger().log(Level.WARNING, "An error occurred while creating data file!", exception);
			}
		}
		try(FileWriter fileWriter = new FileWriter(dataFile, false)) {
			gson.toJson(dataSet, fileWriter);
			fileWriter.flush();
		}catch(IOException exception) {
			plugin.getLogger().log(Level.WARNING, "An error occurred while saving data!", exception);
		}
	}
}