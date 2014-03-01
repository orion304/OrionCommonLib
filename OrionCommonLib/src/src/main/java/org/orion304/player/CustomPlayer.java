package src.main.java.org.orion304.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class CustomPlayer {

	protected String playerName;
	protected Player player = null;

	/**
	 * DO NOT USE THIS CONSTRUCTOR. It is for one extremely specific use in the
	 * CustomPlayerHandler class, to get around the inability to instantiate
	 * generics.
	 */
	public CustomPlayer() {

	}

	/**
	 * Creates a new CustomPlayer with the name of the specified player, and
	 * attach the player object to it.
	 * 
	 * @param player
	 *            The player to wrap.
	 */
	public CustomPlayer(Player player) {
		this(player.getName());
		this.player = player;
	}

	/**
	 * Creates a new CustomPlayer with the specified name.
	 * 
	 * @param playerName
	 *            The name of the CustomPlayer.
	 */
	public CustomPlayer(String playerName) {
		initialize(playerName);
	}

	/**
	 * Clears the contents of the player's inventory, including their armoring.
	 */
	public void clearInventory() {
		if (this.player == null) {
			return;
		}
		PlayerInventory inventory = this.player.getInventory();
		inventory.clear();
		inventory.setArmorContents(null);
	}

	/**
	 * Gets the OfflinePlayer represented by this CustomPlayer's name.
	 * 
	 * @return The OfflinePlayer.
	 */
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(this.playerName);
	}

	/**
	 * Gets the Player that is online with this CustomPlayer's name. Returns
	 * null if there is no player online. The player object is automatically
	 * updated when players leave and join.
	 * 
	 * @return The Player.
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Method to be overridden by subclasses of CustomPlayer, used for their
	 * instantiation.
	 */
	public void initialize() {
		// Used in classes that extend CustomPlayer.
	}

	/**
	 * DO NOT USE THIS METHOD. It is for one extremely specific use in the
	 * CustomPlayerHandler class, to get around the inability to instantiate
	 * generics.
	 * 
	 * @param playerName
	 *            The player's name to set.
	 */
	public void initialize(String playerName) {
		this.playerName = playerName;
		initialize();
	}

	/**
	 * Refreshes the Player object attached to this CustomPlayer by having
	 * Bukkit fetch the online player.
	 */
	void resetPlayer() {
		this.player = Bukkit.getPlayerExact(this.playerName);
	}

	/**
	 * Sets the Player object attached to this CustomPlayer, for use in
	 * getPlayer().
	 * 
	 * @param player
	 *            The player to set.
	 */
	void setPlayer(Player player) {
		this.player = player;
	}

}
