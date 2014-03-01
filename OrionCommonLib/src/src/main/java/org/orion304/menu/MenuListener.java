package src.main.java.org.orion304.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

	private final List<Menu> menus = new ArrayList<>();

	public void addMenu(Menu menu) {
		if (!this.menus.contains(menu)) {
			this.menus.add(menu);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		for (Menu menu : this.menus) {
			if (menu.getInventory().equals(inventory)) {
				MenuItemClickEvent menuEvent = new MenuItemClickEvent(
						event.getSlot(), menu, event.getWhoClicked());
				Bukkit.getPluginManager().callEvent(menuEvent);
				event.setCancelled(true);
				return;
			}
		}
	}

	public void removeMenu(Menu menu) {
		this.menus.remove(menu);
	}

}
