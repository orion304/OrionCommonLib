package src.main.java.org.orion304.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuItemClickEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private final int slot;
	private final Menu menu;
	private final Player player;

	public MenuItemClickEvent(int slot, Menu menu, Player player) {
		this.slot = slot;
		this.menu = menu;
		this.player = player;
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

	public Player getWhoClicked() {
		return this.player;
	}
}
