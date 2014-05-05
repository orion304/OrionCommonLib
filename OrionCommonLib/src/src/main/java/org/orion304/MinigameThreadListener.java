package src.main.java.org.orion304;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import src.main.java.org.orion304.menu.Menu;
import src.main.java.org.orion304.menu.MenuItemClickEvent;

public class MinigameThreadListener implements Listener {

	private final MinigameThread parent;

	public MinigameThreadListener(MinigameThread minigameThread) {
		this.parent = minigameThread;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (event.getWhoClicked() instanceof Player) ? (Player) event
				.getWhoClicked() : null;
		ItemStack item = event.getCurrentItem();
		if (this.parent.handleInteract(player, item)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMenuClick(MenuItemClickEvent event) {
		Player player = event.getWhoClicked();
		int i = event.getSlot();
		Menu menu = event.getMenu();
		this.parent.menuClick(menu, i, player);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		if (this.parent.handleInteract(player, item)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		this.parent.handleInteract(player, item);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {

		if (this.parent.state == GameState.OFF) {
			final int num = this.parent.playerThreshold
					- Bukkit.getOnlinePlayers().length;
			if (num > 0) {
				new BukkitRunnable() {

					@Override
					public void run() {
						Bukkit.getServer().broadcastMessage(
								ChatColor.GRAY + "The game will start once "
										+ ChatColor.RED + ChatColor.UNDERLINE
										+ num + ChatColor.GRAY.toString()
										+ " more players join!");

					}

				}.runTaskLater(this.parent.plugin, 2L);

			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (World w : this.parent.plugin.getServer().getWorlds()) {
			String wname = w.getName();
			new File(wname + "/players/" + event.getPlayer().getName() + ".dat")
					.delete();
		}
	}

}
