package de.cuzim1tigaaa.findmystation.data;

import de.cuzim1tigaaa.findmystation.FindMyStation;

public class Metrics {

	public Metrics(FindMyStation plugin) {
		org.bstats.bukkit.Metrics metrics = new org.bstats.bukkit.Metrics(plugin, 20773);
	}
}