package src.main.java.org.orion304.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import src.main.java.org.orion304.utils.MathUtils;

public class Menu {

	private final List<MenuItem> items = new ArrayList<>();

	private final Inventory inventory;

	public Menu(String title, int slots) {
		this.inventory = Bukkit.createInventory(null,
				MathUtils.getMultipleOfNine(slots), title);
	}

	public Menu(String title, int slots, Collection<MenuItem> items) {
		this(title, slots);
		addMenuItems(items);
	}

	public Menu(String title, int slots, MenuItem... items) {
		this(title, slots, Arrays.asList(items));
	}

	public void addMenuItem(int slot, MenuItem item) {
		this.items.add(item);
		this.inventory.setItem(slot, item.getItem());
		update();
	}

	public void addMenuItem(MenuItem item) {
		addMenuItem(this.inventory.firstEmpty(), item);
	}

	public void addMenuItems(Collection<MenuItem> items) {
		for (MenuItem item : items) {
			addMenuItem(item);
		}
	}

	public void addMenuItems(MenuItem... items) {
		addMenuItems(Arrays.asList(items));
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public void openMenu(Player player) {
		player.openInventory(this.inventory);
	}

	private void update() {
		for (HumanEntity entity : this.inventory.getViewers()) {
			entity.closeInventory();
			entity.openInventory(this.inventory);
		}
	}

}
