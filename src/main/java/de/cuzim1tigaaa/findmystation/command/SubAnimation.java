package de.cuzim1tigaaa.findmystation.command;

import de.cuzim1tigaaa.findmystation.FindMyStation;
import de.cuzim1tigaaa.findmystation.SubCommand;
import de.cuzim1tigaaa.findmystation.data.Animation;
import de.cuzim1tigaaa.findmystation.data.Data;
import de.cuzim1tigaaa.findmystation.files.Messages;
import de.cuzim1tigaaa.findmystation.files.Paths;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SubAnimation implements SubCommand {

	private final FindMyStation plugin;

	public SubAnimation(FindMyStation plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getCommand() {
		return "animation";
	}

	@Override
	public String getPermission() {
		return Paths.PERM_COMMAND_ANIMATION;
	}

	@Override
	public List<String> getAliases() {
		return List.of("animations");
	}

	@Override
	public String getUsage() {
		return "animation [disable|enable|list|select|toggle]";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof Player player))
			return;

		if(!sender.hasPermission(Paths.PERM_COMMAND_ANIMATION)) {
			sender.sendMessage(Messages.getMessage(Paths.MESSAGE_NO_PERMISSION));
			return;
		}
		Data.PlayerData data = plugin.getData().getData(player.getUniqueId());
		if(args.length == 1) {
			player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_CURRENT, "ANIMATION", data.getAnimation()));
			if(!data.isUseAnimation())
				player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_WARNING));
			return;
		}

		switch(args[1]) {
			case "disable" -> {
				data.setUseAnimation(false);
				player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_DISABLE, "STATUS", ChatColor.RED + "disabled"));
			}
			case "enable" -> {
				data.setUseAnimation(true);
				player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_ENABLE, "STATUS", ChatColor.GREEN + "enabled"));
			}
			case "list" -> {
				if(!sender.hasPermission(Paths.PERM_COMMAND_ANIMATION_LIST)) {
					sender.sendMessage(Messages.getMessage(Paths.MESSAGE_NO_PERMISSION));
					return;
				}
				List<Animation> animations = plugin.getCustomColors().getAnimations().values().stream()
						.filter(animation -> !animation.isRequirePermission() || player.hasPermission(Paths.PERM_SPECIFIC_ANIMATION + animation.getName())).toList();
				if(animations.isEmpty()) {
					player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_NONE));
					return;
				}
				ComponentBuilder cb = new ComponentBuilder();
				cb.append(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_LIST));
				animations.forEach(animation -> {
					cb.append("\n");
					cb.append(ChatColor.GRAY + " » ");
					cb.append(ChatColor.YELLOW + animation.getName())
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_CLICK))))
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fms animation select " + animation.getName()));
				});
				player.spigot().sendMessage(cb.create());
			}
			case "select" -> {
				if(!sender.hasPermission(Paths.PERM_COMMAND_ANIMATION_SELECT)) {
					sender.sendMessage(Messages.getMessage(Paths.MESSAGE_NO_PERMISSION));
					return;
				}
				if(args.length > 2) {
					String animationName = args[2];
					Animation animation;
					if((animation = plugin.getCustomColors().getAnimations().getOrDefault(animationName, null)) == null) {
						player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_INVALID, "ANIMATION", args[2]));
						return;
					}
					if(animation.isRequirePermission() && !player.hasPermission(Paths.PERM_SPECIFIC_ANIMATION + animation.getName())) {
						player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_INVALID, "ANIMATION", args[2]));
						return;
					}
					data.setAnimation(animation.getName());
					player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_NEW, "ANIMATION", animation.getName()));
					if(!data.isUseAnimation())
						player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_WARNING));
					return;
				}
				player.sendMessage(org.bukkit.ChatColor.GRAY + " » " + org.bukkit.ChatColor.RED + "/findmystation animation select <Animation>");
			}
			case "toggle" -> {
				data.setUseAnimation(!data.isUseAnimation());
				if(data.isUseAnimation()) player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_ENABLE, "STATUS", ChatColor.GREEN + "enabled"));
				else player.sendMessage(Messages.getMessage(Paths.MESSAGE_COMMAND_ANIMATION_DISABLE, "STATUS", ChatColor.RED + "disabled"));
			}
			default -> player.sendMessage(org.bukkit.ChatColor.GRAY + " » " + org.bukkit.ChatColor.RED + "/findmystation " + getUsage());
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length == 2)
			return List.of("disable", "enable", "list", "select", "toggle");
		if(args.length == 3)
			return plugin.getCustomColors().getAnimations().values()
					.stream().filter(a -> !a.isRequirePermission() || sender.hasPermission(Paths.PERM_SPECIFIC_ANIMATION + a.getName()))
					.map(Animation::getName).toList();
		return Collections.emptyList();
	}
}