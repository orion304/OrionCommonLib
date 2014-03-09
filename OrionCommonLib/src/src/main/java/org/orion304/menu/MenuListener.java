package src.main.java.org.orion304.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

	private final List<Menu> menus = new ArrayList<>();

	public void addMenu(Menu menu) {
		if (!this.menus.contains(menu)) {
			this.menus.add(menu);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		if (inventory == null) {
			return;
		}
		if (event.getSlotType() != SlotType.CONTAINER) {
			return;
		}
		for (Menu menu : this.menus) {
			if (menu.getInventory().getTitle().equals(inventory.getTitle())) {
				MenuItemClickEvent menuEvent = new MenuItemClickEvent(
						event.getRawSlot(), menu, event.getWhoClicked());
				HumanEntity entity = event.getWhoClicked();
				entity.closeInventory();
				entity.openInventory(inventory);
				Bukkit.getPluginManager().callEvent(menuEvent);
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryInteract(InventoryInteractEvent event) {
		Inventory inventory = event.getView().getTopInventory();
		if (inventory == null) {
			return;
		}
		for (Menu menu : this.menus) {
			if (menu.getInventory().getTitle().equals(inventory.getTitle())) {
				event.setCancelled(true);
				return;
			}
		}

	}

	public void removeMenu(Menu menu) {
		this.menus.remove(menu);
	}

}
