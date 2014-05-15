package src.main.java.org.orion304.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import src.main.java.org.orion304.OrionPlugin;

public class CustomPlayerListener implements Listener {

	private final CustomPlayerHandler<? extends OrionPlugin, ? extends CustomPlayer<? extends OrionPlugin>> handler;

	private final Map<UUID, CustomPlayer<? extends OrionPlugin>> players = new ConcurrentHashMap<>();

	/**
	 * Creates a new CustomPlayerListener, which will listen to all relevant
	 * player events on a MONITOR level and keep track of which Player is in
	 * which CustomPlayer.
	 * 
	 * @param handler
	 *            The CustomPlayerHandler which handles all the CustomPlayers.
	 */
	public CustomPlayerListener(
			CustomPlayerHandler<? extends OrionPlugin, ? extends CustomPlayer<? extends OrionPlugin>> handler) {
		this.handler = handler;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		CustomPlayer<? extends OrionPlugin> player = this.handler
				.newCustomPlayer(event);
		this.players.put(event.getUniqueId(), player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAsyncPlayerPreLoginMonitor(AsyncPlayerPreLoginEvent event) {
		UUID id = event.getUniqueId();
		if (this.players.containsKey(id)
				&& event.getLoginResult() != Result.ALLOWED) {
			this.players.remove(id);
		}
	}

	/**
	 * Marks the player for no packet receiving for a brief time.
	 * 
	 * @param event
	 *            The player death event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		CustomPlayer<? extends OrionPlugin> customPlayer = this.handler
				.getCustomPlayer(player);
		customPlayer.noPacketTime = System.currentTimeMillis();
	}

	/**
	 * Handles the player XP change event to keep XP consistent through
	 * countdowns.
	 * 
	 * @param event
	 *            The XP change event.
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		Player player = event.getPlayer();
		CustomPlayer<? extends OrionPlugin> customPlayer = this.handler
				.getCustomPlayer(player);
		if (customPlayer.addExp(event.getAmount())) {
			event.setAmount(0);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CustomPlayer<? extends OrionPlugin> customPlayer = this.handler
				.getCustomPlayer(player);
		if (customPlayer.useItem(player.getInventory().getHeldItemSlot(),
				event.getItem(), event.getAction())) {
			event.setCancelled(true);
		}
	}

	/**
	 * Sets the reference to a player in the CustomPlayer object when the player
	 * joins the server.
	 * 
	 * @param event
	 *            The server join event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID id = player.getUniqueId();
		CustomPlayer<? extends OrionPlugin> customPlayer = this.players.get(id);
		this.handler.setPlayerOnJoin(customPlayer, player);
		customPlayer.refreshHolograms();
		customPlayer.noPacketTime = System.currentTimeMillis();
	}

	/**
	 * Removes the player from memory when they quit, so that the handler only
	 * contains players who are online.
	 * 
	 * @param event
	 *            The player quit event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		removePlayer(player);
	}

	/**
	 * Sets the reference to a player in the CustomPlayer object when the player
	 * respawns.
	 * 
	 * @param event
	 *            The respawn event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		setNewPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		CustomPlayer<? extends OrionPlugin> customPlayer = this.handler
				.getCustomPlayer(player);
		customPlayer.knownEntities.clear();
		customPlayer.noPacketTime = System.currentTimeMillis();
		customPlayer.refreshHolograms();
	}

	/**
	 * The method for removing the reference to a Player in the CustomPlayer
	 * object.
	 * 
	 * @param player
	 *            The Player to remove.
	 */
	private void removePlayer(Player player) {
		UUID playerUUID = player.getUniqueId();
		this.handler.removeCustomPlayer(playerUUID);
	}

	/**
	 * The method for setting the reference to a Player in the CustomPlayer
	 * object.
	 * 
	 * @param player
	 *            The Player to set.
	 */
	private void setNewPlayer(Player player) {
		UUID playerUUID = player.getUniqueId();
		CustomPlayer<? extends OrionPlugin> customPlayer = this.handler
				.getCustomPlayer(playerUUID);
		customPlayer.setPlayer(player);
		customPlayer.refreshHolograms();
	}

}
