package src.main.java.org.orion304.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import src.main.java.org.orion304.Countdown;
import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.utils.Hologram;

public class CustomPlayerHandler<T extends CustomPlayer> implements Runnable {

	private final ConcurrentHashMap<UUID, T> players = new ConcurrentHashMap<>();

	private final Class<T> customPlayerClass;
	private final OrionPlugin plugin;

	private final List<Countdown> globalCountdowns = new ArrayList<>();

	private final Map<Hologram, Location> globalHolograms = new HashMap<>();

	/**
	 * Creates a CustomPlayerHandler object, parameterized by a class which
	 * extends the CustomPlayer class, for use in the CustomPlayerListener, so
	 * that the CustomPlayer objects always have the correct Player object
	 * references in them, and only contains players who are online.
	 * 
	 * @param customPlayerClass
	 *            The class of CustomPlayer for the whole system.
	 */
	public CustomPlayerHandler(OrionPlugin plugin, Class<T> customPlayerClass) {
		this.plugin = plugin;
		this.customPlayerClass = customPlayerClass;
		T.setPlugin(plugin);

		for (Player player : Bukkit.getOnlinePlayers()) {
			newCustomPlayer(player.getUniqueId());
		}

		CustomPlayerListener listener = new CustomPlayerListener(this);
		SpectatorListener specListener = new SpectatorListener(this);
		Bukkit.getPluginManager().registerEvents(listener, this.plugin);
		Bukkit.getPluginManager().registerEvents(specListener, this.plugin);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1L);
	}

	/**
	 * Adds a countdown to the player's XP bar.
	 * 
	 * @param duration
	 *            The duration of the countdown.
	 */
	public void addGlobalCountdown(long duration) {
		addGlobalCountdown(duration, false);
	}

	/**
	 * Adds a countdown to the player's XP bar, with an option for counting up.
	 * 
	 * @param duration
	 *            The duration of the countdown.
	 * @param countUp
	 *            If true, it's counts up to the duration instead.
	 */
	public void addGlobalCountdown(long duration, boolean countUp) {
		Countdown countdown = new Countdown(duration, null, countUp);
		int i = Collections.binarySearch(this.globalCountdowns, countdown);
		if (i < 0) {
			i = ~i;
		}

		this.globalCountdowns.add(i, countdown);
		for (T player : this.players.values()) {
			player.addCountdown(countdown.copy(player.player));
		}
	}

	public T getCustomPlayer(Player player) {
		return getCustomPlayer(player.getUniqueId());
	}

	/**
	 * Returns the CustomPlayer specified by the type in the constructor, by the
	 * playerUUID key. If that CustomPlayer didn't exist, it handles the
	 * creation of a new one.
	 * 
	 * @param playerUUID
	 *            The UUID of the CustomPlayer object.
	 * @return The CustomPlayer.
	 */
	public T getCustomPlayer(UUID playerUUID) {
		if (this.players.containsKey(playerUUID)) {
			return this.players.get(playerUUID);
		}
		return newCustomPlayer(playerUUID);
	}

	/**
	 * Returns a collection of all custom players in memory.
	 * 
	 * @return The collection of all custom players in memory.
	 */
	public Collection<T> getCustomPlayers() {
		return this.players.values();
	}

	/**
	 * This method handles all the weird things that need to be done to
	 * instantiate a new generic type, and makes sure the object is properly
	 * initialized. NOTE: Any subclasses of CustomPlayer *must* overload the
	 * initialize() methods in order to function properly in this handler.
	 * 
	 * @param playerName
	 *            The name of the player to attach to the object.
	 * @return The new CustomPlayer.
	 */
	private T newCustomPlayer(UUID playerUUID) {
		try {
			T newCustomPlayer = this.customPlayerClass.newInstance();
			newCustomPlayer.initialize(playerUUID);
			for (Countdown countdown : this.globalCountdowns) {
				newCustomPlayer.addCountdown(countdown
						.copy(newCustomPlayer.player));
			}
			for (Hologram hologram : this.globalHolograms.keySet()) {
				Location location = this.globalHolograms.get(hologram);
				newCustomPlayer.showHologram(hologram, location);
			}
			this.players.put(playerUUID, newCustomPlayer);
			return newCustomPlayer;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Removes the player from the handler, if that player exists.
	 * 
	 * @param playerUUID
	 *            The player's name to remove.
	 */
	public void removeCustomPlayer(UUID playerUUID) {
		if (this.players.containsKey(playerUUID)) {
			CustomPlayer player = this.players.get(playerUUID);
			player.save();
			player.remove();
			player.setPlayer(null);
			this.players.remove(playerUUID);
		}
	}

	@Override
	public void run() {
		for (T player : this.players.values()) {
			player.run();
		}

		while (!this.globalCountdowns.isEmpty()
				&& !this.globalCountdowns.get(0).isActive()) {
			this.globalCountdowns.remove(0);
		}
	}

	public void showHologram(Hologram hologram, Location location) {
		this.globalHolograms.put(hologram, location);
		for (CustomPlayer player : this.players.values()) {
			player.showHologram(hologram, location);
		}
	}

}
