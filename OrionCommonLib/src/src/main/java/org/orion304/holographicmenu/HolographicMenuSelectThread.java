package src.main.java.org.orion304.holographicmenu;

import org.bukkit.entity.Player;

import src.main.java.org.orion304.OrionPlugin;

public class HolographicMenuSelectThread implements Runnable {

	private final OrionPlugin plugin;

	public HolographicMenuSelectThread(OrionPlugin instance) {
		this.plugin = instance;
	}

	@Override
	public void run() {
		for (Player player : this.plugin.holographicMenuListener.menus.keySet()) {
			HolographicMenu menu = this.plugin.holographicMenuListener.menus
					.get(player);
			menu.boldChoice();
		}

	}

}
