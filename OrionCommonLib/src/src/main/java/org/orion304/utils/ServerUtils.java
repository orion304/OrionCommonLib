package src.main.java.org.orion304.utils;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

public class ServerUtils {

	private static VerbosityLevel serverVerbosityLevel = VerbosityLevel.NONE;

	public static String getString(Object... objects) {
		StringBuilder builder = new StringBuilder();
		for (Object object : objects) {
			builder.append(object);
		}
		return builder.toString();
	}

	/**
	 * Returns true if the player is null or if the player has the permission.
	 * 
	 * @param player
	 *            The player to check (null means it's the server).
	 * @param permission
	 *            The permission to check.
	 * @return True if the player has permission, or is null.
	 */
	public static boolean hasPermission(Player player, String permission) {
		if (player == null) {
			return true;
		}
		return player.hasPermission(permission);
	}

	/**
	 * Sends a message to the player if player isn't null, or prints the message
	 * to the server at a Verbosity Level of NONE otherwise.
	 * 
	 * @param player
	 *            The player to send the message to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendMessage(Player player, String message) {
		if (player == null) {
			verbose(message);
		} else {
			player.sendMessage(message);
		}
	}

	/**
	 * Sets the verbosity level of the server, for use in ServerUtils.verbose
	 * methods.
	 * 
	 * @param level
	 *            The level to set the server to.
	 */
	public static void setVerbosityLevel(VerbosityLevel level) {
		serverVerbosityLevel = level;
	}

	/**
	 * Prints the object to the console, regardless of the configured verbosity
	 * level. Equivalent to verbose(something, VerbosityLevel.NONE).
	 * 
	 * @param something
	 *            The object to print to the console.
	 */
	public static void verbose(Object something) {
		verbose(something, VerbosityLevel.NONE);
	}

	/**
	 * Prints the object to the console, if the level is less than or equal to
	 * the server's verbosity level.
	 * 
	 * @param something
	 *            The object to print to the console.
	 * @param level
	 *            The corresponding verbosity level of this report.
	 */
	public static void verbose(Object something, VerbosityLevel level) {
		boolean report = false;
		if (serverVerbosityLevel == VerbosityLevel.NONE
				&& (level == VerbosityLevel.NONE)) {
			report = true;
		}

		if (serverVerbosityLevel == VerbosityLevel.LOW
				&& (level == VerbosityLevel.NONE || level == VerbosityLevel.LOW)) {
			report = true;
		}

		if (serverVerbosityLevel == VerbosityLevel.MEDIUM
				&& (level == VerbosityLevel.NONE || level == VerbosityLevel.LOW || level == VerbosityLevel.MEDIUM)) {
			report = true;
		}

		if (serverVerbosityLevel == VerbosityLevel.HIGH
				&& (level == VerbosityLevel.NONE || level == VerbosityLevel.LOW
						|| level == VerbosityLevel.MEDIUM || level == VerbosityLevel.HIGH)) {
			report = true;
		}

		if (report) {
			if (something == null) {
				something = "null";
			}
			Logger.getLogger("Feedback").info(
					"[" + level.name() + "] " + something.toString());
		}

	}

}
