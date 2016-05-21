package src.main.java.org.orion304.player;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_9_R1.Packet;
import src.main.java.org.orion304.Countdown;
import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.utils.MathUtils;

public abstract class CustomPlayer<T extends OrionPlugin> {

	final protected UUID playerUUID;
	final protected T plugin;

	protected Player player = null;

	protected boolean isSpectating = false;
	protected boolean hasVoted = false;
	protected int timesVoted = 0;
	private final List<Countdown> countdowns = new ArrayList<>();
	final List<Packet> packets = new ArrayList<>();
	final ConcurrentHashMap<Long, List<Packet>> futurePackets = new ConcurrentHashMap<>();
	final List<Hologram> holograms = new ArrayList<>();
	protected long tick = 0;

	private int packetHandlerTaskId = -1;

	final List<String> knownEntities = new ArrayList<>();
	long noPacketTime = 0;
	PrintWriter writer = null;

	protected Vector velocity = new Vector(0, 0, 0);
	private Location previouslocation = null;

	// /**
	// * DO NOT USE THIS CONSTRUCTOR. It is for one extremely specific use in
	// the
	// * CustomPlayerHandler class, to get around the inability to instantiate
	// * generics.
	// */
	// public CustomPlayer() {
	//
	// }

	// /**
	// * Creates a new CustomPlayer with the name of the specified player, and
	// * attach the player object to it.
	// *
	// * @param player
	// * The player to wrap.
	// */
	// public CustomPlayer(Player player, CustomPlayerHandler<? extends
	// CustomPlayer, ? extends OrionPlugin> handler) {
	// this(player.getUniqueId(), handler);
	// this.player = player;
	// }

	/**
	 * Creates a new CustomPlayer with the specified UUID, attached to a
	 * handler.
	 *
	 * @param playerUUID
	 *            The UUID of the CustomPlayer.
	 */
	public CustomPlayer(UUID playerUUID, CustomPlayerHandler<T, ? extends CustomPlayer<T>> handler) {
		this.playerUUID = playerUUID;
		this.plugin = handler.getPlugin();
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

	private void addPacket(Collection<? extends Packet> packets) {
		synchronized (this.packets) {
			this.packets.addAll(packets);
		}
	}

	protected abstract void asyncPreLogin(AsyncPlayerPreLoginEvent event);

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

	private void clearKnownEntities() {
		synchronized (this.knownEntities) {
			this.knownEntities.clear();
		}
	}

	void clearPackets() {
		synchronized (this.packets) {
			this.packets.clear();
		}
	}

	void construct() {
		// resetPlayer();
		clearKnownEntities();
		this.noPacketTime = System.currentTimeMillis();
		if (this.player.getName().equalsIgnoreCase("orion304")) {
			try {
				this.writer = new PrintWriter("orion304.log");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		initialize();
		this.packetHandlerTaskId = Bukkit.getScheduler()
				.runTaskTimerAsynchronously(this.plugin, new CustomPlayerPacketManager(this), 1L, 1L).getTaskId();
	}

	/**
	 * Method called when the player leaves.
	 */
	abstract public void destroy();

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
		@SuppressWarnings("unchecked")
		CustomPlayer<? extends OrionPlugin> other = (CustomPlayer<? extends OrionPlugin>) obj;
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

	public T getPlugin() {
		return this.plugin;
	}

	/**
	 * Gets the UUID associated with this player.
	 *
	 * @return The UUID of the player.
	 */
	public UUID getUUID() {
		return this.playerUUID;
	}

	public Vector getVelocity() {
		return this.velocity.clone();
	}

	/**
	 * This method is run every tick to handle the player, if need be.
	 */
	protected abstract void handle();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.playerUUID == null) ? 0 : this.playerUUID.hashCode());
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
	 * Checks if the player is spectating.
	 *
	 * @return True if the player is spectating.
	 */
	protected boolean isSpectating() {
		return this.isSpectating;
	}

	/**
	 * Checks if the CustomPlayer knows about an entity sent via packets.
	 *
	 * @param id
	 *            The entity to check for.
	 * @return True if the CustomPlayer knows of that entity
	 */
	public boolean knowsAbout(int id) {
		String value = String.valueOf(id);
		synchronized (this.knownEntities) {
			return this.knownEntities.contains(value);
		}
	}

	abstract protected void login(PlayerLoginEvent event);

	/**
	 * Returns the maximum number of times the player can vote.
	 *
	 * @return
	 */
	abstract protected int maxVotes();

	void refreshHolograms() {
		for (Hologram hologram : this.holograms) {
			hologram.show(this);
		}
	}

	/**
	 * Method called when the player leaves.
	 */
	void remove() {
		clearKnownEntities();
		for (Hologram hologram : this.holograms) {
			hologram.clearPlayer(this);
		}
		if (this.writer != null) {
			this.writer.close();
		}
		destroy();
		if (this.packetHandlerTaskId != -1) {
			Bukkit.getScheduler().cancelTask(this.packetHandlerTaskId);
		}
	}

	public void removeHologram(Hologram hologram) {
		if (this.holograms.contains(hologram)) {
			this.holograms.remove(hologram);
			hologram.destroy(this);
		}
	}

	/**
	 * Refreshes the Player object attached to this CustomPlayer by having
	 * Bukkit fetch the online player.
	 */
	protected void resetPlayer() {
		this.player = Bukkit.getPlayer(this.playerUUID);
	}

	void run() {
		this.tick++;

		if (!this.countdowns.isEmpty()) {
			Countdown countdown = this.countdowns.get(0);
			countdown.run();
			if (!countdown.isActive()) {
				this.countdowns.remove(0);
			}
		}

		Location location = this.player.getLocation();
		if (this.previouslocation != null) {
			if (location.getWorld().equals(this.previouslocation.getWorld())) {
				double length = this.previouslocation.distanceSquared(location);
				if (length > 0) {
					this.velocity = MathUtils.getDistanceVector(this.previouslocation, location);
				}
			} else {
				this.velocity = new Vector(0, 0, 0);
			}
		}
		this.previouslocation = location.clone();
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
	 * Sends a packet to this player.
	 *
	 * @param packets
	 *            The packets to send.
	 */
	public void sendPacket(Collection<? extends Packet> packets) {
		sendPacket(0, packets);
	}

	/**
	 * Sends a packet to this player.
	 *
	 * @param packets
	 *            The packets to send.
	 */
	public void sendPacket(int delay, Packet... packets) {
		sendPacket(delay, Arrays.asList(packets));
	}

	/**
	 * Sends a packet to this player.
	 *
	 * @param packets
	 *            The packets to send.
	 */
	public void sendPacket(long delay, Collection<? extends Packet> packets) {
		if (delay == 0) {
			addPacket(packets);
		} else {
			long t = this.tick + delay;
			if (this.futurePackets.containsKey(t)) {
				this.futurePackets.get(t).addAll(packets);
			} else {
				List<Packet> list = new ArrayList<>();
				list.addAll(packets);
				this.futurePackets.put(t, list);
			}
		}

	}

	/**
	 * Sends a packet to this player.
	 *
	 * @param packets
	 *            The packets to send.
	 */
	public void sendPacket(Packet... packets) {
		sendPacket(Arrays.asList(packets));
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
	 * Shows a hologram to this player, and keeps track of it through world
	 * changes and deaths.
	 *
	 * @param hologram
	 *            The hologram to show.
	 * @param location
	 *            The location of the hologram.
	 */
	public void showHologram(Hologram hologram) {
		if (!this.holograms.contains(hologram)) {
			this.holograms.add(hologram);
		}
		refreshHolograms();
	}

	/**
	 * Shows a hologram to this player, and keeps track of it through world
	 * changes and deaths.
	 *
	 * @param hologram
	 *            The hologram to show.
	 * @param location
	 *            The location of the hologram.
	 */
	public void showHologram(final Hologram hologram, long ticks) {
		if (!this.holograms.contains(hologram)) {
			this.holograms.add(hologram);
		}
		refreshHolograms();
		new BukkitRunnable() {
			@Override
			public void run() {
				removeHologram(hologram);
			}
		}.runTaskLater(this.plugin, ticks + 2L);
	}

	/**
	 * Methods called when the player is to remove spectating status.
	 */
	abstract protected void turnOffSpectating();

	/**
	 * Methods called when the player is to become a spectator.
	 */
	abstract protected void turnOnSpectating();

	abstract public boolean useItem(int slot, ItemStack item, Action action);

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
