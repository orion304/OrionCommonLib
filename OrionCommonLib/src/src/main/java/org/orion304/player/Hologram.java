package src.main.java.org.orion304.player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.EntityHorse;
import net.minecraft.server.v1_7_R3.EntityWitherSkull;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R3.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.holographicmenu.HolographicMenuChoice;
import src.main.java.org.orion304.utils.Justification;
import src.main.java.org.orion304.utils.MathUtils;

public class Hologram {
	private static final double distance = 0.23;
	private static final double offset = -1.4;

	private final List<String> lines = new ArrayList<String>();

	private final Map<UUID, Boolean> showing = new HashMap<>();

	private Location location;
	private final OrionPlugin plugin;

	private final List<Packet> showPackets = new ArrayList<>();
	private final List<Packet> destroyPackets = new ArrayList<>();
	private final List<EntityWitherSkull> skulls = new ArrayList<>();
	private final List<EntityHorse> horses = new ArrayList<>();

	public Hologram(OrionPlugin plugin, Location location,
			Justification justify, String... lines) {
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
				double formatsize = (maxsize - size) / whitespacelength
						+ line.length();
				this.lines.add(String.format(format + Math.round(formatsize)
						+ "s", lines[i]));
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
		initialize(plugin);
	}

	public Hologram(OrionPlugin plugin, Location location, String... lines) {
		this(plugin, location, Justification.NONE, lines);
	}

	public void boldChoice(HolographicMenuChoice choice) {
		for (int i = 0; i < this.horses.size(); i++) {
			EntityHorse horse = this.horses.get(i);
			String line = this.lines.get(i);
			if (i == choice.getIndex()) {
				line = ChatColor.BOLD + this.lines.get(i);
			}

			if (line != horse.getCustomName()) {
				changeLine(i, line);
			}
		}
	}

	public void changeLine(int i, String line) {
		if (i < this.horses.size()) {
			EntityHorse horse = this.horses.get(i);
			if (line == horse.getCustomName()) {
				return;
			}
			horse.setCustomName(line);
			Packet packet = new PacketPlayOutEntityMetadata(horse.getId(),
					horse.getDataWatcher(), true);
			for (UUID id : this.showing.keySet()) {
				CustomPlayer player = this.plugin.getCustomPlayerHandler()
						.getCustomPlayer(id);
				player.sendPacket(packet);
			}
		} else {
			try {
				throw new Exception("Tried to change a line that doesn't exist");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void clearPlayer(CustomPlayer player) {
		this.showing.remove(player.getUUID());
	}

	public void destroy() {
		for (CustomPlayer player : this.plugin.getCustomPlayerHandler()
				.getCustomPlayers()) {
			destroy(player);
		}

		this.showing.clear();
	}

	void destroy(CustomPlayer player) {
		UUID id = player.getUUID();
		if (this.showing.get(id)) {
			player.sendPacket(this.destroyPackets);
			this.showing.remove(id);
		}
	}

	public HolographicMenuChoice getBestChoice(Location point, Vector line) {
		Location bestLocation = null, loc;
		double bestDistance = Double.MAX_VALUE, dis;
		String bestString = null;
		int bestIndex = -1;
		int size = this.lines.size();
		double dy;
		for (int i = 0; i < size; i++) {
			dy = -offset + (size - 1 - i) * distance;
			loc = this.location.clone().add(0, dy, 0);
			dis = MathUtils.getDistanceFromLine(line, point, loc);
			if (dis < bestDistance) {
				bestDistance = dis;
				bestIndex = i;
				bestLocation = loc;
				bestString = this.lines.get(i);
			}
		}
		return new HolographicMenuChoice(bestLocation, bestDistance,
				bestString, this, bestIndex);
	}

	private void initialize(JavaPlugin plugin) {
		WorldServer world = ((CraftWorld) this.location.getWorld()).getHandle();
		for (String text : this.lines) {
			EntityWitherSkull skull = new EntityWitherSkull(world);
			this.skulls.add(skull);

			EntityHorse horse = new EntityHorse(world);
			horse.setAge(-1700000);
			horse.setCustomName(text);
			horse.setCustomNameVisible(true);
			this.horses.add(horse);
		}
		setLocation(this.location);
	}

	public void move(Location newLocation) {
		if (this.location.distance(newLocation) < .2) {
			return;
		}
		try {
			Field a = PacketPlayOutEntityTeleport.class.getDeclaredField("a");
			Field b = PacketPlayOutEntityTeleport.class.getDeclaredField("b");
			Field c = PacketPlayOutEntityTeleport.class.getDeclaredField("c");
			Field d = PacketPlayOutEntityTeleport.class.getDeclaredField("d");
			Field e = PacketPlayOutEntityTeleport.class.getDeclaredField("e");
			Field f = PacketPlayOutEntityTeleport.class.getDeclaredField("f");
			for (Field field : new Field[] { a, b, c, d, e, f }) {
				field.setAccessible(true);
			}
			setLocation(newLocation);
			List<Packet> packets = new ArrayList<>();
			Location loc = this.location.clone().add(0,
					(this.lines.size() / 2) * distance, 0);
			for (int i = 0; i < this.skulls.size(); i++) {
				EntityWitherSkull skull = this.skulls.get(i);
				EntityHorse horse = this.horses.get(i);
				Packet packet;

				packet = new PacketPlayOutEntityTeleport();
				a.setInt(packet, horse.getId());
				b.setInt(packet, (int) Math.floor(newLocation.getX() * 32.0D));
				c.setInt(packet,
						(int) Math.floor((newLocation.getY() + 55) * 32.0D));
				d.setInt(packet, (int) Math.floor(newLocation.getZ() * 32.0D));
				e.setByte(packet, (byte) 0);
				f.setByte(packet, (byte) 0);
				packets.add(packet);

				packet = new PacketPlayOutEntityTeleport();
				a.setInt(packet, skull.getId());
				b.setInt(packet, (int) Math.floor(newLocation.getX() * 32.0D));
				c.setInt(packet,
						(int) Math.floor((newLocation.getY() + 0 + 55) * 32.0D));
				d.setInt(packet, (int) Math.floor(newLocation.getZ() * 32.0D));
				e.setByte(packet, (byte) 0);
				f.setByte(packet, (byte) 0);
				packets.add(packet);

				loc.subtract(0, distance, 0);

			}
			for (UUID id : this.showing.keySet()) {
				CustomPlayer player = this.plugin.getCustomPlayerHandler()
						.getCustomPlayer(id);
				player.showHologram(this);
				// player.sendPacket(packets);
			}
			for (Field field : new Field[] { a, b, c, d, e, f }) {
				field.setAccessible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setLocation(Location location) {
		this.location = location;
		this.showPackets.clear();
		this.destroyPackets.clear();
		Location loc = this.location.clone().add(0,
				(this.lines.size() / 2) * distance, 0);
		for (int i = 0; i < this.horses.size(); i++) {
			EntityWitherSkull skull = this.skulls.get(i);
			skull.setLocation(loc.getX(), loc.getY() + 1 + 55, loc.getZ(), 0, 0);
			PacketPlayOutSpawnEntity packet_skull = new PacketPlayOutSpawnEntity(
					skull, 66);
			EntityHorse horse = this.horses.get(i);
			horse.setLocation(loc.getX(), loc.getY() + 55, loc.getZ(), 0, 0);

			PacketPlayOutSpawnEntityLiving packedt = new PacketPlayOutSpawnEntityLiving(
					horse);

			PacketPlayOutAttachEntity pa = new PacketPlayOutAttachEntity(0,
					horse, skull);
			this.showPackets.add(packedt);
			this.showPackets.add(packet_skull);
			this.showPackets.add(pa);
			Packet destroy = new PacketPlayOutEntityDestroy(skull.getId(),
					horse.getId());
			this.destroyPackets.add(destroy);
			loc.subtract(0, distance, 0);
		}
	}

	void show(CustomPlayer player) {
		if (this.location.getWorld().equals(player.getPlayer().getWorld())) {
			player.sendPacket(this.showPackets);
			this.showing.put(player.getUUID(), true);
		} else {
			this.showing.remove(player.getUUID());
		}
	}

	void show(CustomPlayer player, long ticks) {
		show(player);
		if (this.location.getWorld().equals(player.getPlayer().getWorld())) {
			player.sendPacket(ticks, this.destroyPackets);
		}
	}
}
