package src.main.java.org.orion304.holographicmenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.player.CustomPlayer;
import src.main.java.org.orion304.player.Hologram;
import src.main.java.org.orion304.utils.ServerUtils;

public class HolographicMenu {

	private final OrionPlugin plugin;
	private final CustomPlayer<? extends OrionPlugin> customPlayer;
	private final Player player;
	private final List<Hologram> panes = new ArrayList<>();

	private Location boldedLocation = null;

	public HolographicMenu(OrionPlugin plugin, CustomPlayer<? extends OrionPlugin> customPlayer) {
		this.plugin = plugin;
		this.customPlayer = customPlayer;
		this.player = customPlayer.getPlayer();
	}

	public Hologram addPane(Location location, List<String> strings) {
		return addPane(location, strings.toArray(new String[strings.size()]));
	}

	public Hologram addPane(Location location, String... strings) {
		Hologram hologram = new Hologram(this.plugin, location, strings);
		this.panes.add(hologram);
		return hologram;
	}

	public void boldChoice() {
		HolographicMenuChoice choice = getChoice();
		if (!choice.getLocation().equals(this.boldedLocation)) {
			Hologram hologram = choice.getHologram();
			for (Hologram holo : this.panes) {
				if (holo.equals(hologram)) {
					holo.boldChoice(choice);
				} else {
					holo.boldChoice(null);
				}
			}

			this.boldedLocation = choice.getLocation();
		}
	}

	public void destroy() {
		for (Hologram hologram : this.panes) {
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

		for (Hologram hologram : this.panes) {
			HolographicMenuChoice choice = hologram.getBestChoice(eyeLocation, line);
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
		for (Hologram hologram : this.panes) {
			this.customPlayer.showHologram(hologram);
		}
		this.plugin.getHolographicMenuListener().addHolographicMenu(this);
		boldChoice();
	}

}
