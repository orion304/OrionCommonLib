package src.main.java.org.orion304.player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import src.main.java.org.orion304.Countdown;
import src.main.java.org.orion304.OrionPlugin;

public class CustomPlayerHandler<U extends OrionPlugin, T extends CustomPlayer<U>>
		implements Runnable {

	private final ConcurrentHashMap<UUID, T> players = new ConcurrentHashMap<>();

	private final Class<T> customPlayerClass;
	private final U plugin;

	private final List<Countdown> globalCountdowns = new ArrayList<>();

	private final List<Hologram> globalHolograms = new ArrayList<>();

	/**
	 * Creates a CustomPlayerHandler object, parameterized by a class which
	 * extends the CustomPlayer class, for use in the CustomPlayerListener, so
	 * that the CustomPlayer objects always have the correct Player object
	 * references in them, and only contains players who are online.
	 * 
	 * @param customPlayerClass
	 *            The class of CustomPlayer for the whole system.
	 */
	public CustomPlayerHandler(U plugin, Class<T> customPlayerClass) {
		this.plugin = plugin;
		this.customPlayerClass = customPlayerClass;

		for (Player player : Bukkit.getOnlinePlayers()) {
			reloadCustomPlayer(player);
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

	public void destroyHologram(Hologram hologram) {
		if (this.globalHolograms.contains(hologram)) {
			hologram.destroy();
		}
	}

	public T getCustomPlayer(Player player) {
		return getCustomPlayer(player.getUniqueId());
	}

	/**
	 * Returns the CustomPlayer specified by the type in the constructor, by the
	 * playerUUID key. If that CustomPlayer didn't exist, return null.
	 * 
	 * @param playerUUID
	 *            The UUID of the CustomPlayer object.
	 * @return The CustomPlayer.
	 */
	public T getCustomPlayer(UUID playerUUID) {
		return this.players.get(playerUUID);
	}

	/**
	 * Returns a collection of all custom players in memory.
	 * 
	 * @return The collection of all custom players in memory.
	 */
	public Collection<T> getCustomPlayers() {
		return this.players.values();
	}

	public U getPlugin() {
		return this.plugin;
	}

	/**
	 * Creates a new CustomPlayer object for storage until the player logs in.
	 * Calls .asyncPreLogin on that object.
	 * 
	 * @param event
	 *            The AsyncPlayerPreLoginEvent associated with them trying to
	 *            come online.
	 * @return The new CustomPlayer.
	 */
	T newCustomPlayer(AsyncPlayerPreLoginEvent event) {
		UUID id = event.getUniqueId();
		T newCustomPlayer = newCustomPlayer(id);
		newCustomPlayer.asyncPreLogin(event);
		return newCustomPlayer;
	}

	/**
	 * Creates a new CustomPlayer object with this ID.
	 * 
	 * @param id
	 *            The UUID of the player
	 * @return The new CustomPlayer object.
	 */
	private T newCustomPlayer(UUID id) {
		try {
			Constructor<T> con = this.customPlayerClass.getConstructor(
					UUID.class, getClass());
			T newCustomPlayer = con.newInstance(id, this);
			return newCustomPlayer;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Used only in the case when the plugin is enabled while there are players
	 * online (i.e. /reload). Handles those players.
	 * 
	 * @param player
	 *            The player to reload.
	 */
	private void reloadCustomPlayer(Player player) {
		AsyncPlayerPreLoginEvent event = new AsyncPlayerPreLoginEvent(
				player.getName(), null, player.getUniqueId());
		T customPlayer = newCustomPlayer(event);
		setPlayerOnJoin(customPlayer, player);
	}

	/**
	 * Removes the player from the handler, if that player exists.
	 * 
	 * @param playerUUID
	 *            The player's name to remove.
	 */
	public void removeCustomPlayer(UUID playerUUID) {
		if (this.players.containsKey(playerUUID)) {
			T player = this.players.get(playerUUID);
			player.save();
			player.remove();
			player.setPlayer(null);
			this.players.remove(playerUUID);
		}
	}

	@Override
	public void run() {
		for (T player : getCustomPlayers()) {
			player.run();
		}

		while (!this.globalCountdowns.isEmpty()
				&& !this.globalCountdowns.get(0).isActive()) {
			this.globalCountdowns.remove(0);
		}
	}

	/**
	 * Sets the player field of the CustomPlayer when they join, associating the
	 * previously created CustomPlayer (by the AsyncPlayerPreLogin) with this
	 * new player and putting it into the handler for use.
	 * 
	 * @param player
	 *            The CustomPlayer object.
	 * @param realPlayer
	 *            The Player object to be associated with it.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	void setPlayerOnJoin(CustomPlayer<? extends OrionPlugin> player,
			Player realPlayer) {
		player.setPlayer(realPlayer);
		player.construct();
		for (Countdown countdown : this.globalCountdowns) {
			player.addCountdown(countdown.copy(player.player));
		}
		for (Hologram hologram : this.globalHolograms) {
			player.showHologram(hologram);
		}
		this.players.put(player.playerUUID, (T) player);
	}

	public void showHologram(Hologram hologram) {
		this.globalHolograms.add(hologram);
		for (T player : getCustomPlayers()) {
			hologram.show(player);
		}
	}

}
