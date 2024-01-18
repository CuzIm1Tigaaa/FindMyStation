package de.cuzim1tigaaa.findmystation;

import de.cuzim1tigaaa.findmystation.data.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;

import java.util.*;

public class HighlightListener implements Listener {

	private final List<Color> rgb = List.of(
			Color.fromRGB(255,  0,      0),
			Color.fromRGB(255,  127,    0),
			Color.fromRGB(255,  255,    0),
			Color.fromRGB(127,  255,    0),
			Color.fromRGB(0,    255,    0),
			Color.fromRGB(0,    255,    127),
			Color.fromRGB(0,    255,    255),
			Color.fromRGB(0,    127,    255),
			Color.fromRGB(0,    0,      255),
			Color.fromRGB(127,  0,      255),
			Color.fromRGB(255,  0,      255),
			Color.fromRGB(255,  0,      127)
	);

	@Getter
	private static class BlockData {

		final Location location;
		private final BlockDisplay blockDisplay;
		@Setter private int taskId;

		public BlockData(org.bukkit.Location location, BlockDisplay blockDisplay) {
			this.location = location;
			this.blockDisplay = blockDisplay;
		}
	}



	private final FindMyStation plugin;
	private final Map<UUID, BlockData> highlighting = new HashMap<>();

	public HighlightListener(FindMyStation plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();

		if(!highlighting.containsKey(uuid))
			return;

		BlockData data = highlighting.get(uuid);
		data.getBlockDisplay().remove();
		Bukkit.getScheduler().cancelTask(data.taskId);
		highlighting.remove(player.getUniqueId());
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();

		if(!highlighting.containsKey(uuid))
			return;

		BlockData data = highlighting.get(uuid);
		if(data.location.equals(event.getBlock().getLocation())) {
			data.getBlockDisplay().remove();
			Bukkit.getScheduler().cancelTask(data.taskId);
			highlighting.remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void merchantOpen(InventoryOpenEvent event) {


	}

	@EventHandler
	public void villagerInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();

		Entity entity = event.getRightClicked();
		if(!(entity instanceof Villager villager))
			return;

		if(!player.hasPermission(Paths.PERMISSION_USE_PLUGIN))
			return;

		if(!player.isSneaking())
			return;

		if(highlighting.containsKey(uuid)) {
			highlighting.get(uuid).getBlockDisplay().remove();
			Bukkit.getScheduler().cancelTask(highlighting.get(uuid).taskId);
			highlighting.remove(player.getUniqueId());
		}

		Location jobBlock = villager.getMemory(MemoryKey.JOB_SITE);
		if(jobBlock == null) {
			if(villager.getProfession() == Villager.Profession.NONE || villager.getProfession() == Villager.Profession.NITWIT) {
				Config.sendActionBarMessage(player, Paths.MESSAGE_NO_JOB);
				return;
			}
			Config.sendActionBarMessage(player, Paths.MESSAGE_NO_STATION);
			return;
		}
		event.setCancelled(true);
		player.closeInventory();

		BlockDisplay display = spawnBlockDisplay(jobBlock.getBlock());
		player.showEntity(plugin, display);

		highlighting.put(uuid, new BlockData(jobBlock, display));
		player.closeInventory();

		Data.PlayerData playerData = plugin.getData().getData(uuid);
		if(Config.getBoolean(Paths.CONFIG_RGB_ALLOW)) {
			if(player.hasPermission(Paths.PERMISSION_ALLOW_RGB) && playerData.isUseRGB()) {
				highlighting.get(uuid).setTaskId(glowRGBColor(player));
				return;
			}
		}
		highlighting.get(uuid).setTaskId(glowSingleColor(player));
	}


	private int glowSingleColor(Player player) {
		final UUID uuid = player.getUniqueId();

		Data.PlayerData playerData = plugin.getData().getData(uuid);
		java.awt.Color color = java.awt.Color.decode(playerData.getHexColor());
		highlighting.get(uuid).getBlockDisplay().setGlowColorOverride(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));

		return Bukkit.getScheduler().runTaskLater(plugin, () -> {
			highlighting.get(uuid).getBlockDisplay().remove();
			highlighting.remove(uuid);
		}, Config.getInteger(Paths.CONFIG_DURATION) * 20L).getTaskId();
	}

	private int glowRGBColor(Player player) {
		final UUID uuid = player.getUniqueId();
		Data.PlayerData playerData = plugin.getData().getData(uuid);
		return new BukkitRunnable() {
			long ticks = Config.getInteger(Paths.CONFIG_DURATION) * 20L;
			int index = 0;
			@Override
			public void run() {
				BlockData data = highlighting.get(uuid);

				if(ticks <= 0) {
					Bukkit.getScheduler().cancelTask(data.getTaskId());
					highlighting.get(uuid).getBlockDisplay().remove();
					highlighting.remove(uuid);
				}
				ticks -= Config.getInteger(Paths.CONFIG_RGB_SPEED);
				data.getBlockDisplay().setGlowColorOverride(rgb.get(index));
				index++;
				if(index == rgb.size())
					index = 0;
			}
		}.runTaskTimer(plugin, 0L, Config.getInteger(Paths.CONFIG_RGB_SPEED)).getTaskId();
	}




	private BlockDisplay spawnBlockDisplay(Block block) {
		BlockDisplay display = (BlockDisplay) block.getWorld().spawnEntity(block.getLocation(), EntityType.BLOCK_DISPLAY);
		display.setBlock(block.getBlockData());

		Transformation transformation = display.getTransformation();
		transformation.getScale().sub(.01f, .01f, .01f);
		transformation.getTranslation().add(.005f, .005f, .005f);
		display.setTransformation(transformation);

		display.setGlowing(true);
		display.setVisibleByDefault(false);
		return display;
	}
}