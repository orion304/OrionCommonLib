package src.main.java.org.orion304.holographicmenu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HolographicMenuListener implements Listener {
	protected final Map<Player, HolographicMenu> menus = new HashMap<>();

	public void addHolographicMenu(HolographicMenu menu) {
		this.menus.put(menu.getPlayer(), menu);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (this.menus.containsKey(player)) {
			this.menus.remove(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (this.menus.containsKey(player)) {
			HolographicMenu menu = this.menus.get(player);
			HolographicMenuClickEvent menuEvent = new HolographicMenuClickEvent(
					menu, player);
			Bukkit.getPluginManager().callEvent(menuEvent);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		// Player player = event.getPlayer();
		// if (this.menus.containsKey(player)) {
		// HolographicMenu menu = this.menus.get(player);
		// menu.boldChoice();
		// }
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (this.menus.containsKey(player)) {
			this.menus.remove(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (this.menus.containsKey(player)) {
			this.menus.remove(player);
		}
	}

	public void removeHolographicMenu(HolographicMenu menu) {
		this.menus.remove(menu.getPlayer());
	}

}
