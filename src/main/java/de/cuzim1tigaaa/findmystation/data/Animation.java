package de.cuzim1tigaaa.findmystation.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;

import java.util.List;

@Getter
public class Animation {

	private final String name;
	@Setter private int interval;
	private final boolean requirePermission;
	private final List<Color> colors;

	public Animation(String name, int interval, boolean requirePermission, List<Color> colors) {
		this.name = name;
		this.interval = interval;
		this.requirePermission = requirePermission;
		this.colors = colors;
	}
}