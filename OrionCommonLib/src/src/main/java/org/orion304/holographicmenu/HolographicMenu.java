package src.main.java.org.orion304.holographicmenu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.utils.Hologram;
import src.main.java.org.orion304.utils.ServerUtils;

public class HolographicMenu {

	private final OrionPlugin plugin;
	private final Player player;
	private final Map<Location, Hologram> panes = new HashMap<>();

	private Location boldedLocation = null;

	public HolographicMenu(OrionPlugin plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}

	public void addPane(Location location, String... strings) {
		Hologram hologram = new Hologram(this.plugin, strings);
		this.panes.put(location, hologram);
	}

	public void boldChoice() {
		HolographicMenuChoice choice = getChoice();
		if (!choice.getLocation().equals(this.boldedLocation)) {
			Hologram hologram = choice.getHologram();
			hologram.boldChoice(choice);

			this.boldedLocation = choice.getLocation();
		}
	}

	public void destroy() {
		for (Location location : this.panes.keySet()) {
			Hologram hologram = this.panes.get(location);
			hologram.destroy();
		}
		this.plugin.getHolographicMenuListener().removeHolographicMenu(this);
	}

	public HolographicMenuChoice getChoice() {
		if (this.player.isDead() || !this.player.isOnline()) {
			return null;
		}
		double bestDistance = Double.MAX_VALUE;
		HolographicMenuChoice bestChoice = null;

		Location eyeLocation = this.player.getEyeLocation();
		Vector line = eyeLocation.getDirection();

		for (Location location : this.panes.keySet()) {
			Hologram hologram = this.panes.get(location);
			HolographicMenuChoice choice = hologram.getBestChoice(eyeLocation,
					line);
			if (choice == null) {
				ServerUtils.verbose("What the fuck?");
				continue;
			}
			if (choice.getDistance() < bestDistance) {
				bestDistance = choice.getDistance();
				bestChoice = choice;
			}
		}
		return bestChoice;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void show() {
		for (Location location : this.panes.keySet()) {
			// ServerUtils.verbose("Show at: " + location);
			Hologram hologram = this.panes.get(location);
			hologram.show(location, this.player);
		}
		this.plugin.getHolographicMenuListener().addHolographicMenu(this);
		boldChoice();
	}

}
