package src.main.java.org.orion304.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import src.main.java.org.orion304.Countdown;
import src.main.java.org.orion304.OrionPlugin;

public abstract class CustomPlayer {

	protected static OrionPlugin plugin;

	public static void setPlugin(OrionPlugin instance) {
		plugin = instance;
	}

	protected UUID playerUUID;

	protected Player player = null;

	protected boolean isSpectating = false;
	protected boolean hasVoted = false;
	protected int timesVoted = 0;
	private final List<Countdown> countdowns = new ArrayList<>();

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
		this(player.getUniqueId());
		this.player = player;
	}

	/**
	 * Creates a new CustomPlayer with the specified UUID.
	 * 
	 * @param playerUUID
	 *            The UUID of the CustomPlayer.
	 */
	public CustomPlayer(UUID playerUUID) {
		initialize(playerUUID);
	}

	void addCountdown(Countdown countdown) {
		int i = Collections.binarySearch(this.countdowns, countdown);
		if (i < 0) {
			i = ~i;
		}
		if (i == 0 && !this.countdowns.isEmpty()) {
			this.countdowns.get(0).end();
			countdown.start();
		}
		this.countdowns.add(i, countdown);

	}

	/**
	 * Adds a countdown to the player's XP bar.
	 * 
	 * @param duration
	 *            The duration of the countdown.
	 */
	public void addCountdown(long duration) {
		addCountdown(duration, false);
	}

	/**
	 * Adds a countdown to the player's XP bar, with an option for counting up.
	 * 
	 * @param duration
	 *            The duration of the countdown.
	 * @param countUp
	 *            If true, it's counts up to the duration instead.
	 */
	public void addCountdown(long duration, boolean countUp) {
		Countdown countdown = new Countdown(duration, this.player, countUp);
		addCountdown(countdown);
	}

	/**
	 * Used in the event where a player's XP changes. Returns true if the event
	 * should be cancelled, it handles the persistence of XP through countdowns.
	 * 
	 * @param xp
	 *            The amount of XP to give.
	 * @return True if the event should be cancelled.
	 */
	boolean addExp(int xp) {
		if (this.countdowns.isEmpty()) {
			return false;
		} else {
			this.countdowns.get(0).addExp(xp);
			return true;
		}
	}

	/**
	 * Checks if the player *can* vote.
	 * 
	 * @return
	 */
	protected boolean canVote() {
		return !this.hasVoted;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CustomPlayer other = (CustomPlayer) obj;
		if (this.playerUUID == null) {
			if (other.playerUUID != null) {
				return false;
			}
		} else if (!this.playerUUID.equals(other.playerUUID)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the Player that is online with this CustomPlayer's UUID. Returns
	 * null if there is no player online. The player object is automatically
	 * updated when players leave and join.
	 * 
	 * @return The Player.
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * This method is run every tick to handle the player, if need be.
	 */
	protected abstract void handle();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.playerUUID == null) ? 0 : this.playerUUID.hashCode());
		return result;
	}

	/**
	 * Checks if the player has voted already.
	 * 
	 * @return
	 */
	protected boolean hasVoted() {
		return this.timesVoted > 0;
	}

	/**
	 * Method to be overridden by subclasses of CustomPlayer, used for their
	 * instantiation.
	 */
	abstract public void initialize();

	/**
	 * DO NOT USE THIS METHOD. It is for one extremely specific use in the
	 * CustomPlayerHandler class, to get around the inability to instantiate
	 * generics.
	 * 
	 * @param playerUUID
	 *            The player's UUID to set.
	 */
	public void initialize(UUID playerUUID) {
		this.playerUUID = playerUUID;
		resetPlayer();
		initialize();
	}

	/**
	 * Checks if the player is spectating.
	 * 
	 * @return True if the player is spectating.
	 */
	protected boolean isSpectating() {
		return this.isSpectating;
	}

	/**
	 * Returns the maximum number of times the player can vote.
	 * 
	 * @return
	 */
	abstract protected int maxVotes();

	/**
	 * Refreshes the Player object attached to this CustomPlayer by having
	 * Bukkit fetch the online player.
	 */
	protected void resetPlayer() {
		this.player = Bukkit.getPlayer(this.playerUUID);
	}

	void run() {
		if (!this.countdowns.isEmpty()) {
			Countdown countdown = this.countdowns.get(0);
			countdown.run();
			if (!countdown.isActive()) {
				this.countdowns.remove(0);
			}
		}
		handle();
	}

	/**
	 * Method called when a player should be saved.
	 */
	abstract protected void save();

	/**
	 * Sends a message to the player.
	 * 
	 * @param string
	 *            The message.
	 */
	public void sendMessage(String string) {
		this.player.sendMessage(string);
	}

	/**
	 * Sets the Player object attached to this CustomPlayer, for use in
	 * getPlayer().
	 * 
	 * @param player
	 *            The player to set.
	 */
	protected void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Makes the player be a spectator, or removes it, as specified by
	 * isSpectating.
	 * 
	 * @param isSpectating
	 *            If true, change the player to a spectator.
	 */
	public void setSpectating(boolean isSpectating) {
		this.isSpectating = isSpectating;
		this.player.setAllowFlight(isSpectating);
		if (isSpectating) {
			turnOnSpectating();
		} else {
			turnOffSpectating();
		}
	}

	/**
	 * Methods called when the player is to remove spectating status.
	 */
	abstract protected void turnOffSpectating();

	/**
	 * Methods called when the player is to become a spectator.
	 */
	abstract protected void turnOnSpectating();

	/**
	 * Method to register that the player has voted.
	 */
	public void vote() {
		this.timesVoted++;
		if (this.timesVoted >= maxVotes()) {
			this.hasVoted = true;
		}
	}

}
