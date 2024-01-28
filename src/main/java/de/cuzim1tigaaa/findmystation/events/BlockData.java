package de.cuzim1tigaaa.findmystation.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;

@Getter
public class BlockData {
	final Location location;
	private final BlockDisplay blockDisplay;
	@Setter
	private int taskId;

	public BlockData(org.bukkit.Location location, BlockDisplay blockDisplay) {
		this.location = location;
		this.blockDisplay = blockDisplay;
	}
}