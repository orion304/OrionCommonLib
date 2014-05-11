package src.main.java.org.orion304.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import src.main.java.org.orion304.utils.StringUtils;

public class MenuItem {

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
			List<String> formattedString = StringUtils.wrapString(string);
			formattedLore.addAll(formattedString);
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
