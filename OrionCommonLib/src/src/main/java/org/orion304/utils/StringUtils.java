package src.main.java.org.orion304.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class StringUtils {

	/**
	 * Concatenates the array of objects' .toString() methods.
	 * 
	 * @param objects
	 *            The array of objects.
	 * @return The string of all the objects' .toString() methods combined.
	 */
	public static String getString(Object... objects) {
		StringBuilder builder = new StringBuilder();
		for (Object object : objects) {
			builder.append(object);
		}
		return builder.toString();
	}

	/**
	 * Wraps the words in the string, such that no line has more than 40
	 * characters.
	 * 
	 * @param string
	 *            The string to format.
	 * @return A list of strings, representing the wrapped string.
	 */
	public static List<String> wrapString(String string) {
		return wrapString(string, 40);
	}

	/**
	 * Wraps the words in the string, such that no line has more characters than
	 * maxStringSize.
	 * 
	 * @param string
	 *            The string to format.
	 * @param maxStringSize
	 *            The maximum number of characters in a line.
	 * @return A list of strings, representing the wrapped string.
	 */
	public static List<String> wrapString(String string, int maxStringSize) {
		List<String> formattedString = new ArrayList<>();
		ChatColor color = ChatColor.getByChar(string.charAt(1));
		String col = (color == null) ? "" : color.toString();
		while (string.length() > maxStringSize) {
			int i = maxStringSize - 1;
			while (true) {
				char c = string.charAt(i);
				if (c == ' ') {
					formattedString.add(string.substring(0, i));
					string = col + string.substring(i);
					break;
				} else if (i == 1) {
					formattedString.add(string.substring(0, 20));
					string = col + " " + string.substring(20);
					break;
				} else {
					i--;
				}
			}
		}
		formattedString.add(string);
		return formattedString;
	}

	public static List<String> wrapStrings(List<String> lore) {
		return wrapStrings(lore, 40);
	}

	public static List<String> wrapStrings(List<String> lore, int maxStringSize) {
		List<String> formattedString = new ArrayList<>();
		for (String string : lore) {
			ChatColor color = ChatColor.getByChar(string.charAt(1));
			String col = (color == null) ? "" : color.toString();
			while (string.length() > maxStringSize) {
				int i = maxStringSize - 1;
				while (true) {
					char c = string.charAt(i);
					if (c == ' ') {
						formattedString.add(string.substring(0, i));
						string = col + string.substring(i);
						break;
					} else if (i == 1) {
						formattedString.add(string.substring(0, 20));
						string = col + " " + string.substring(20);
						break;
					} else {
						i--;
					}
				}
			}
			formattedString.add(string);
		}
		return formattedString;
	}

}
