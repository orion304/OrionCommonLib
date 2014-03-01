package src.main.java.org.orion304.menu;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuItemClickEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private final int slot;
	private final Menu menu;
	private final HumanEntity entity;

	public MenuItemClickEvent(int slot, Menu menu, HumanEntity entity) {
		this.slot = slot;
		this.menu = menu;
		this.entity = entity;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Menu getMenu() {
		return this.menu;
	}

	public int getSlot() {
		return this.slot;
	}

	public HumanEntity getWhoClicked() {
		return this.entity;
	}
}
