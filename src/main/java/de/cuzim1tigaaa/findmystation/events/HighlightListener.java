package de.cuzim1tigaaa.findmystation.events;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import de.cuzim1tigaaa.findmystation.data.*;
import de.cuzim1tigaaa.findmystation.files.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;

import java.util.*;

public class HighlightListener implements Listener {

	private final FindMyStation plugin;
	private final Map<UUID, BlockData> highlighting;

	public HighlightListener(FindMyStation plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);

		this.highlighting = new HashMap<>();
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();

		if(!highlighting.containsKey(uuid))
			return;

		BlockData data = highlighting.get(uuid);
		data.getBlockDisplay().remove();
		Bukkit.getScheduler().cancelTask(data.getTaskId());
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
			Bukkit.getScheduler().cancelTask(data.getTaskId());
			highlighting.remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void villagerInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();

		Entity entity = event.getRightClicked();
		if(!(entity instanceof Villager villager))
			return;

		if(!player.hasPermission(Paths.PERM_USE_PLUGIN))
			return;

		if(!player.isSneaking())
			return;

		Location jobBlock = villager.getMemory(MemoryKey.JOB_SITE);
		if(jobBlock == null) {
			if(villager.getProfession() == Villager.Profession.NONE || villager.getProfession() == Villager.Profession.NITWIT) {
				Config.sendActionBarMessage(player, Paths.MESSAGE_NO_JOB);
				return;
			}
			Config.sendActionBarMessage(player, Paths.MESSAGE_NO_STATION);
			return;
		}
		if(highlighting.containsKey(uuid)) {
			highlighting.get(uuid).getBlockDisplay().remove();
			Bukkit.getScheduler().cancelTask(highlighting.get(uuid).getTaskId());
			highlighting.remove(player.getUniqueId());
		}

		event.setCancelled(true);
		player.closeInventory();

		BlockDisplay display = spawnBlockDisplay(jobBlock.getBlock());
		player.showEntity(plugin, display);

		highlighting.put(uuid, new BlockData(jobBlock, display));
		player.closeInventory();

		Data.PlayerData playerData = plugin.getData().getData(uuid);
		if(Config.getBoolean(Paths.CONFIG_ALLOW_ANIMATIONS) && playerData.isUseAnimation()) {
			Animation animation;
			if((animation = plugin.getCustomColors().getAnimations().get(playerData.getAnimation())) == null) {
				highlighting.get(uuid).setTaskId(glowSingleColor(player));
				return;
			}
			if(!animation.isRequirePermission()) {
				highlighting.get(uuid).setTaskId(glowMultiColor(player, playerData.getAnimation()));
				return;
			}
			if(player.hasPermission(Paths.PERM_ALLOW_ANIMATIONS) && player.hasPermission(Paths.PERM_SPECIFIC_ANIMATION + animation.getName())) {
				highlighting.get(uuid).setTaskId(glowMultiColor(player, playerData.getAnimation()));
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


	private int glowMultiColor(Player player, String identifier) {
		final UUID uuid = player.getUniqueId();
		Animation animation = plugin.getCustomColors().getAnimations().get(identifier);
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
				ticks -= animation.getInterval();
				data.getBlockDisplay().setGlowColorOverride(animation.getColors().get(index));
				index++;
				if(index == animation.getColors().size())
					index = 0;
			}
		}.runTaskTimer(plugin, 0L, animation.getInterval()).getTaskId();
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