package src.main.java.org.orion304.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_7_R3.EntityHorse;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EntityWitherSkull;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import net.minecraft.server.v1_7_R3.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import src.main.java.org.orion304.holographicmenu.HolographicMenuChoice;

public class HologramOld {
	private static final double distance = 0.23;
	// private static final double offset = -1.4;

	private final List<String> lines = new ArrayList<String>();

	private final List<Integer> ids = new ArrayList<Integer>();
	private boolean showing = false;
	private Player player;

	private Location location;
	private final JavaPlugin plugin;
	private final String[] constructLines;

	private final Justification justify;
	private final List<Packet> packets = new ArrayList<>();

	public HologramOld(JavaPlugin plugin, Justification justify,
			String... lines) {
		this.constructLines = lines;
		this.justify = justify;
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
	}

	public HologramOld(JavaPlugin plugin, Player chatter, String... lines) {
		this(plugin, lines);
	}

	public HologramOld(JavaPlugin plugin, String... lines) {
		this(plugin, Justification.NONE, lines);
	}

	public void boldChoice(HolographicMenuChoice choice) {
		// ServerUtils.verbose("Bolding");
		for (int i = 0; i < this.lines.size(); i++) {
			String line = this.lines.get(i);
			line = line.replaceAll(ChatColor.BOLD.toString(), "");
			if (choice.getIndex() == i) {
				line = ChatColor.BOLD + line;
				// ServerUtils.verbose(line);
			}
			this.lines.set(i, line);
		}
		Location loc = this.location.clone();
		destroy();
		show(loc, this.player);
	}

	/*
	 * public void change(String... lines) { destroy(); this.lines =
	 * Arrays.asList(lines); show(this.location); }
	 */

	@Override
	public HologramOld clone() {
		return new HologramOld(this.plugin, this.justify, this.constructLines);
	}

	public void destroy() {

		if (this.showing == false) {
			this.location = null;
			return;
			/*
			 * try { throw new Exception("Isn't showing!"); } catch (Exception
			 * e) { e.printStackTrace(); }
			 */
		}

		int[] ints = new int[this.ids.size()];
		for (int j = 0; j < ints.length; j++) {
			ints[j] = this.ids.get(j);
		}

		for (Entity ent : this.location.getWorld().getEntities()) {
			if (ent.getType() == EntityType.WITHER_SKULL) {
				// System.out.println("WITHER_SKULL @ " +
				// ent.getLocation().toString());
				if (ent.getLocation().distanceSquared(this.location) <= 4) {
					ent.remove();
				}
			}
		}

		final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
				ints);

		// this.plugin.getServer().getScheduler()
		// .runTaskLaterAsynchronously(this.plugin, new Runnable() {
		// @Override
		// public void run() {
		// for (Player player : Bukkit.getOnlinePlayers()) {
		// ((CraftPlayer) player).getHandle().playerConnection
		// .sendPacket(packet);
		// }
		// }
		// }, 2L);

		for (Player player : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) player).getHandle().playerConnection
					.sendPacket(packet);
		}

		this.showing = false;
		this.location = null;

	}

	// public HolographicMenuChoice getBestChoice(Location point, Vector line) {
	// Location bestLocation = null, loc;
	// double bestDistance = Double.MAX_VALUE, dis;
	// String bestString = null;
	// int bestIndex = -1;
	// int size = this.lines.size();
	// double dy;
	// for (int i = 0; i < size; i++) {
	// dy = -offset + (size - 1 - i) * distance;
	// loc = this.location.clone().add(0, dy, 0);
	// dis = MathUtils.getDistanceFromLine(line, point, loc);
	// if (dis < bestDistance) {
	// bestDistance = dis;
	// bestIndex = i;
	// bestLocation = loc;
	// bestString = this.lines.get(i);
	// }
	// }
	// return new HolographicMenuChoice(bestLocation, bestDistance,
	// bestString, this, bestIndex);
	// }

	public boolean isShowing() {
		return this.showing;
	}

	/**
	 * ONLY DO THIS IF THE CLIENT HAS FORGOTTEN ABOUT THE FIRST PACKETS.
	 * 
	 * @param target
	 *            Player to show it to again.
	 */
	public void reshow(Player target) {
		PlayerConnection conn = ((CraftPlayer) target).getHandle().playerConnection;
		for (Packet packet : this.packets) {
			conn.sendPacket(packet);
		}
	}

	public void show(Location loc) {
		show(loc, null);
	}

	public void show(Location loc, long ticks, Player single_target) {
		show(loc, single_target);
		new BukkitRunnable() {
			@Override
			public void run() {
				destroy();
			}
		}.runTaskLater(this.plugin, ticks);
	}

	public void show(Location loc, Player single_target) {
		this.packets.clear();
		if (this.showing == true) {
			try {
				throw new Exception("Is already showing!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.player = single_target;
		Location first = loc.clone().add(0, (this.lines.size() / 2) * distance,
				0);
		for (int i = 0; i < this.lines.size(); i++) {
			this.ids.addAll(showLine(this.plugin, first.clone(),
					this.lines.get(i), single_target));
			first.subtract(0, distance, 0);
		}
		this.showing = true;
		this.location = loc;
	}

	private List<Integer> showLine(JavaPlugin plugin, final Location loc,
			String text, final Player single_target) {
		WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
		final EntityWitherSkull skull = new EntityWitherSkull(world);
		skull.setLocation(loc.getX(), loc.getY() + 1 + 55, loc.getZ(), 0, 0);
		// ((CraftWorld) loc.getWorld()).getHandle().addEntity(skull);
		final PacketPlayOutSpawnEntity packet_skull = new PacketPlayOutSpawnEntity(
				skull, 66);

		final EntityHorse horse = new EntityHorse(world);
		horse.setLocation(loc.getX(), loc.getY() + 55, loc.getZ(), 0, 0);
		horse.setAge(-1700000);
		horse.setCustomName(text);
		horse.setCustomNameVisible(true);
		final PacketPlayOutSpawnEntityLiving packedt = new PacketPlayOutSpawnEntityLiving(
				horse);

		plugin.getServer().getScheduler()
				.runTaskLaterAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						if (single_target == null) {
							for (Player player : loc.getWorld().getPlayers()) {
								EntityPlayer nmsPlayer = ((CraftPlayer) player)
										.getHandle();
								nmsPlayer.playerConnection.sendPacket(packedt);
								nmsPlayer.playerConnection
										.sendPacket(packet_skull);

								PacketPlayOutAttachEntity pa = new PacketPlayOutAttachEntity(
										0, horse, skull);
								nmsPlayer.playerConnection.sendPacket(pa);
							}
						} else {
							EntityPlayer nmsPlayer = ((CraftPlayer) single_target)
									.getHandle();
							nmsPlayer.playerConnection.sendPacket(packedt);
							nmsPlayer.playerConnection.sendPacket(packet_skull);

							PacketPlayOutAttachEntity pa = new PacketPlayOutAttachEntity(
									0, horse, skull);
							nmsPlayer.playerConnection.sendPacket(pa);
							HologramOld.this.packets.add(packedt);
							HologramOld.this.packets.add(packet_skull);
							HologramOld.this.packets.add(pa);
						}
					}
				}, 4L);

		return Arrays.asList(skull.getId(), horse.getId());
	}

}
