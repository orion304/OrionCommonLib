package src.main.java.org.orion304.holographicmenu;

import org.bukkit.Location;

import src.main.java.org.orion304.player.Hologram;

public class HolographicMenuChoice {

	private final Location location;
	private final double distance;
	private final String string;
	private final Hologram hologram;
	private final int index;

	public HolographicMenuChoice(Location location, double distance,
			String string, Hologram hologram, int index) {
		this.location = location;
		this.distance = distance;
		this.string = string;
		this.hologram = hologram;
		this.index = index;
	}

	public double getDistance() {
		return this.distance;
	}

	public Hologram getHologram() {
		return this.hologram;
	}

	public int getIndex() {
		return this.index;
	}

	public Location getLocation() {
		return this.location;
	}

	public String getString() {
		return this.string;
	}

	@Override
	public String toString() {
		return "HolographicMenuChoice [location=" + this.location
				+ ", distance=" + this.distance + ", string=" + this.string
				+ ", hologram=" + this.hologram + ", index=" + this.index + "]";
	}

}
