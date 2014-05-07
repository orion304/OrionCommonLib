package src.main.java.org.orion304.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class MenuListener implements Listener {

	private final List<Menu> menus = new ArrayList<>();

	public void addMenu(Menu menu) {
		if (!this.menus.contains(menu)) {
			this.menus.add(menu);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		InventoryView view = event.getView();
		Inventory inventory = view.getTopInventory();
		Player player;
		if (event.getWhoClicked() instanceof Player) {
			player = (Player) event.getWhoClicked();
		} else {
			return;
		}
		if (inventory == null) {
			return;
		}
		if (event.getClickedInventory() == null) {
			return;
		}
		for (Menu menu : this.menus) {
			if (menu.getInventory().equals(inventory)) {
				HumanEntity entity = event.getWhoClicked();
				entity.closeInventory();
				entity.openInventory(inventory);
				if (event.getSlotType() == SlotType.CONTAINER) {
					MenuItemClickEvent menuEvent = new MenuItemClickEvent(
							event.getRawSlot(), menu, player);
					Bukkit.getPluginManager().callEvent(menuEvent);
					event.setCancelled(true);
				}
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		HumanEntity entity = event.getPlayer();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			InventoryView view = event.getView();
			Inventory inventory = view.getTopInventory();
			if (inventory == null) {
				return;
			}
			for (Menu menu : this.menus) {
				if (menu.getInventory().equals(inventory)) {
					MenuCloseEvent menuEvent = new MenuCloseEvent(menu, player);
					Bukkit.getPluginManager().callEvent(menuEvent);
					return;
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryInteract(InventoryInteractEvent event) {
		InventoryView view = event.getView();
		Inventory inventory = view.getTopInventory();
		if (inventory == null) {
			return;
		}
		for (Menu menu : this.menus) {
			if (menu.getInventory().equals(inventory)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	public void removeMenu(Menu menu) {
		this.menus.remove(menu);
	}

}
