package src.main.java.org.orion304.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.fakeentity.FakeArmorStand;
import src.main.java.org.orion304.holographicmenu.HolographicMenuChoice;
import src.main.java.org.orion304.utils.Justification;
import src.main.java.org.orion304.utils.MathUtils;

public class Hologram {
	public static final double distance = 0.23;
	private static final double offset = -0.38;

	private final List<String> lines = new ArrayList<String>();

	private final Map<UUID, Boolean> showing = new HashMap<>();

	private Location location;
	private final OrionPlugin plugin;

	private final List<FakeArmorStand> armorStands = new ArrayList<>();

	public Hologram(OrionPlugin plugin, Location location, Justification justify, String... lines) {
		if (justify != Justification.NONE && lines.length != 0) {
			double maxsize = 0;
			double whitespacelength = 2. / 3.;
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				double chars = line.replaceAll(" ", "").length();
				double white = (line.length() - chars) * whitespacelength;
				double size = chars + white;
				if (size > maxsize) {
					maxsize = size;
				}
			}
			String format = "%1$";
			if (justify == Justification.LEFT) {
				format += "-";
			}
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				double chars = line.replaceAll(" ", "").length();
				double white = (line.length() - chars) * whitespacelength;
				double size = white + chars;
				double formatsize = (maxsize - size) / whitespacelength + line.length();
				this.lines.add(String.format(format + Math.round(formatsize) + "s", lines[i]));
				// ServerUtils.verbose(chars + " " + (line.length() - chars) +
				// " "
				// + (chars + (formatsize - chars) * whitespacelength)
				// + " " + formatsize);
			}
		} else {
			this.lines.addAll(Arrays.asList(lines));
		}
		this.plugin = plugin;
		this.location = location;
		initialize();
	}

	public Hologram(OrionPlugin plugin, Location location, String... lines) {
		this(plugin, location, Justification.NONE, lines);
	}

	public void boldChoice(HolographicMenuChoice choice) {
		for (int i = 0; i < this.armorStands.size(); i++) {
			FakeArmorStand armorStand = this.armorStands.get(i);
			ArmorStand as = armorStand.getArmorStand();
			String line = this.lines.get(i);
			if ((choice != null) && (i == choice.getIndex())) {
				if (line.charAt(0) == '§') {
					line = line.substring(0, 2) + ChatColor.BOLD + line.substring(2);
				} else {
					line = ChatColor.BOLD + this.lines.get(i);
				}
			}
			if (line != as.getCustomName()) {
				changeLine(i, line);
			}
		}
	}

	public void changeLine(int i, String line) {
		if (i < this.armorStands.size()) {
			FakeArmorStand armorStand = this.armorStands.get(i);
			ArmorStand as = armorStand.getArmorStand();
			if (line == as.getCustomName()) {
				return;
			}
			as.setCustomName(line);
			armorStand.updateMetadata();
		} else {
			try {
				throw new Exception("Tried to change a line that doesn't exist");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void clearPlayer(CustomPlayer<? extends OrionPlugin> player) {
		for (FakeArmorStand armorStand : this.armorStands) {
			armorStand.despawnTo(player.getPlayer());
		}
	}

	public void destroy() {
		for (CustomPlayer<? extends OrionPlugin> player : this.plugin.getCustomPlayerHandler().getCustomPlayers()) {
			player.removeHologram(this);
		}

		this.showing.clear();
	}

	void destroy(CustomPlayer<? extends OrionPlugin> player) {
		UUID id = player.getUUID();
		if (this.showing.get(id)) {
			for (FakeArmorStand armorStand : this.armorStands) {
				armorStand.despawnTo(player.getPlayer());
			}
			this.showing.remove(id);
		}
	}

	public HolographicMenuChoice getBestChoice(Location point, Vector line) {
		Location bestLocation = null, loc;
		double bestDistance = Double.MAX_VALUE, dis;
		String bestString = null;
		int bestIndex = -1;
		int size = this.lines.size();
		loc = this.location.clone();
		for (int i = 0; i < size; i++) {
			dis = MathUtils.getDistanceFromLine(line, point, loc);
			if (dis < bestDistance) {
				bestDistance = dis;
				bestIndex = i;
				bestLocation = loc.clone();
				bestString = this.lines.get(i);
			}
			loc.subtract(0, distance, 0);
		}
		return new HolographicMenuChoice(bestLocation, bestDistance, bestString, this, bestIndex);
	}

	private void initialize() {
		World world = this.location.getWorld();
		Location loc = this.location.clone();
		loc.add(0, offset, 0);
		for (String text : this.lines) {
			FakeArmorStand armorStand = new FakeArmorStand(plugin, world, loc, new Vector(0, 0, 0));
			ArmorStand as = armorStand.getArmorStand();
			as.setCustomName(text);
			as.setCustomNameVisible(true);
			this.armorStands.add(armorStand);
			loc.subtract(0, distance, 0);
		}
		setLocation(this.location);
	}

	public void move(Location newLocation) {
		if (this.location.distanceSquared(newLocation) < .0016) {
			return;
		} else {
			setLocation(newLocation);
		}
	}

	private void setLocation(Location location) {
		this.location = location;
		Location loc = this.location.clone();
		loc.add(0, offset, 0);
		for (int i = 0; i < this.armorStands.size(); i++) {
			FakeArmorStand armorStand = this.armorStands.get(i);
			armorStand.setLocation(loc);
			loc.subtract(0, distance, 0);
		}
	}

	public void show(CustomPlayer<? extends OrionPlugin> player) {
		if (this.location.getWorld().equals(player.getPlayer().getWorld())) {
			for (FakeArmorStand armorStand : this.armorStands) {
				armorStand.spawnTo(player.getPlayer());
			}
			this.showing.put(player.getUUID(), true);
		} else {
			this.showing.remove(player.getUUID());
		}
	}

	public void show(CustomPlayer<? extends OrionPlugin> player, long ticks) {
		show(player);
		if (this.location.getWorld().equals(player.getPlayer().getWorld())) {
			for (FakeArmorStand armorStand : this.armorStands) {
				armorStand.despawnToAfter(ticks, player.getPlayer());
			}
		}
	}
}
