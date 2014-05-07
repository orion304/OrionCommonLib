package src.main.java.org.orion304.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuCloseEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private final Menu menu;
	private final Player player;

	public MenuCloseEvent(Menu menu, Player player) {
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

	public Player getWhoClosed() {
		return this.player;
	}

}
