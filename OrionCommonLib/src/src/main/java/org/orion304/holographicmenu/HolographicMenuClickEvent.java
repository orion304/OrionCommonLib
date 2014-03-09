package src.main.java.org.orion304.holographicmenu;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HolographicMenuClickEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private final HolographicMenu menu;
	private final HumanEntity entity;

	public HolographicMenuClickEvent(HolographicMenu menu, HumanEntity entity) {
		this.menu = menu;
		this.entity = entity;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public HolographicMenu getMenu() {
		return this.menu;
	}

	public HumanEntity getWhoClicked() {
		return this.entity;
	}
}
