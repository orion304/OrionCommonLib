package src.main.java.org.orion304.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class CustomPlayerListener implements Listener {

	private final CustomPlayerHandler<? extends CustomPlayer> handler;

	/**
	 * Creates a new CustomPlayerListener, which will listen to all relevant
	 * player events on a MONITOR level and keep track of which Player is in
	 * which CustomPlayer.
	 * 
	 * @param handler
	 *            The CustomPlayerHandler which handles all the CustomPlayers.
	 */
	public CustomPlayerListener(
			CustomPlayerHandler<? extends CustomPlayer> handler) {
		this.handler = handler;
	}

	/**
	 * Clears the reference to a player in the CustomPlayer object after the
	 * player dies.
	 * 
	 * @param event
	 *            The death event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		removePlayer(event.getEntity());
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
		setNewPlayer(event.getPlayer());
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

	/**
	 * The method for removing the reference to a Player in the CustomPlayer
	 * object.
	 * 
	 * @param player
	 *            The Player to remove.
	 */
	private void removePlayer(Player player) {
		String name = player.getName();
		CustomPlayer customPlayer = this.handler.getCustomPlayer(name);
		customPlayer.setPlayer(null);
	}

	/**
	 * The method for setting the reference to a Player in the CustomPlayer
	 * object.
	 * 
	 * @param player
	 *            The Player to set.
	 */
	private void setNewPlayer(Player player) {
		String name = player.getName();
		CustomPlayer customPlayer = this.handler.getCustomPlayer(name);
		customPlayer.setPlayer(player);
	}

}
