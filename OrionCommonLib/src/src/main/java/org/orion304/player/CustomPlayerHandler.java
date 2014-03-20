package src.main.java.org.orion304.player;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class CustomPlayerHandler<T extends CustomPlayer> {

	private ConcurrentHashMap<String, T> players = new ConcurrentHashMap<>();

	private final Class<T> customPlayerClass;

	/**
	 * Creates a CustomPlayerHandler object, parameterized by a class which
	 * extends the CustomPlayer class, for use in the CustomPlayerListener, so
	 * that the CustomPlayer objects always have the correct Player object
	 * references in them.
	 * 
	 * @param customPlayerClass
	 *            The class of CustomPlayer for the whole system.
	 */
	public CustomPlayerHandler(Class<T> customPlayerClass) {
		this.customPlayerClass = customPlayerClass;
		this.players = new ConcurrentHashMap<>();
	}

	/**
	 * Returns the CustomPlayer specified by the type in the constructor, by the
	 * playerName key. If that CustomPlayer didn't exist, it handles the
	 * creation of a new one.
	 * 
	 * @param playerName
	 *            The name of the CustomPlayer object.
	 * @return The CustomPlayer.
	 */
	public T getCustomPlayer(String playerName) {
		if (this.players.containsKey(playerName)) {
			return this.players.get(playerName);
		}
		return newCustomPlayer(playerName);
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
	 * initialize() and initialize(playerName) methods in order to function
	 * properly in this handler.
	 * 
	 * @param playerName
	 *            The name of the player to attach to the object.
	 * @return The new CustomPlayer.
	 */
	private T newCustomPlayer(String playerName) {
		try {
			T newCustomPlayer = this.customPlayerClass.newInstance();
			newCustomPlayer.initialize(playerName);
			this.players.put(playerName, newCustomPlayer);
			return newCustomPlayer;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

}
