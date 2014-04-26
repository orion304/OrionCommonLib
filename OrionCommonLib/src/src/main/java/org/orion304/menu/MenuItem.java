package src.main.java.org.orion304.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuItem {

	private static final int maxStringSize = 40;

	private final ItemStack item;

	public MenuItem(String itemName, Material itemType, int stackSize) {
		this(itemName, itemType, stackSize, (byte) 0);
	}

	public MenuItem(String itemName, Material itemType, int stackSize, byte data) {
		this.item = new ItemStack(itemType, stackSize, data);
		ItemMeta meta = this.item.getItemMeta();
		meta.setDisplayName(itemName);
		this.item.setItemMeta(meta);
	}

	public MenuItem(String itemName, Material itemType, int stackSize,
			byte data, List<String> lore) {
		this(itemName, itemType, stackSize, data);
		ItemMeta meta = this.item.getItemMeta();
		List<String> formattedLore = new ArrayList<>();
		for (String string : lore) {
			ChatColor color = ChatColor.getByChar(string.charAt(1));
			String col = (color == null) ? "" : color.toString();
			while (string.length() > maxStringSize) {
				int i = maxStringSize - 1;
				while (true) {
					char c = string.charAt(i);
					if (c == ' ') {
						formattedLore.add(string.substring(0, i));
						string = col + string.substring(i);
						break;
					} else if (i == 1) {
						formattedLore.add(string.substring(0, 20));
						string = col + " " + string.substring(20);
						break;
					} else {
						i--;
					}
				}
			}
			formattedLore.add(string);
		}
		meta.setLore(formattedLore);
		this.item.setItemMeta(meta);
	}

	public MenuItem(String itemName, Material itemType, int stackSize,
			byte data, String... lore) {
		this(itemName, itemType, stackSize, data, Arrays.asList(lore));
	}

	public MenuItem(String itemName, Material itemType, int stackSize,
			List<String> lore) {
		this(itemName, itemType, stackSize, (byte) 0, lore);
	}

	public MenuItem(String itemName, Material itemType, int stackSize,
			String... lore) {
		this(itemName, itemType, stackSize, (byte) 0, lore);
	}

	public ItemStack getItem() {
		return this.item;
	}

}
