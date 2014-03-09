package src.main.java.org.orion304.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_7_R1.EntityHorse;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.EntityWitherSkull;
import net.minecraft.server.v1_7_R1.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R1.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import src.main.java.org.orion304.holographicmenu.HolographicMenuChoice;

public class Hologram {
	private static final double distance = 0.23;

	private static List<Integer> showLine(Location loc, String text, Player p) {
		WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
		EntityWitherSkull skull = new EntityWitherSkull(world);
		skull.setLocation(loc.getX(), loc.getY() + 1 + 55, loc.getZ(), 0, 0);
		((CraftWorld) loc.getWorld()).getHandle().addEntity(skull);

		EntityHorse horse = new EntityHorse(world);
		horse.setLocation(loc.getX(), loc.getY() + 55, loc.getZ(), 0, 0);
		horse.setAge(-1700000);
		horse.setCustomName(text);
		horse.setCustomNameVisible(true);
		PacketPlayOutSpawnEntityLiving packedt = new PacketPlayOutSpawnEntityLiving(
				horse);
		List<Player> players = new ArrayList<Player>();
		if (p == null) {
			players.addAll(loc.getWorld().getPlayers());
		} else {
			players.add(p);
		}
		for (Player player : players) {
			EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
			nmsPlayer.playerConnection.sendPacket(packedt);

			PacketPlayOutAttachEntity pa = new PacketPlayOutAttachEntity(0,
					horse, skull);
			nmsPlayer.playerConnection.sendPacket(pa);
		}
		return Arrays.asList(skull.getId(), horse.getId());
	}

	private List<String> lines = new ArrayList<String>();
	private final List<Integer> ids = new ArrayList<Integer>();
	private boolean showing = false;

	private Location location;
	private final JavaPlugin plugin;

	private Player player = null;

	public Hologram(JavaPlugin plugin, String... lines) {
		this.lines.addAll(Arrays.asList(lines));
		this.plugin = plugin;
	}

	public void boldChoice(HolographicMenuChoice choice) {
		int index = choice.getIndex();
		for (int i = 0; i < this.lines.size(); i++) {
			if (i == index) {
				this.lines.set(index, ChatColor.BOLD + this.lines.get(index));
			} else {
				this.lines.set(i, ChatColor.stripColor(this.lines.get(i)));
			}
		}
		change(this.lines.toArray(new String[this.lines.size()]));
	}

	public void change(String... lines) {
		destroy();
		this.lines = Arrays.asList(lines);
		show(this.location);
	}

	public void destroy() {
		if (this.showing == false) {
			try {
				throw new Exception("Isn't showing!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int[] ints = new int[this.ids.size()];
		for (int j = 0; j < ints.length; j++) {
			ints[j] = this.ids.get(j);
		}
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ints);
		for (Player player : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) player).getHandle().playerConnection
					.sendPacket(packet);
		}
		this.showing = false;
	}

	public HolographicMenuChoice getBestChoice(Location eyeLocation, Vector line) {
		double bestDistance = Double.MAX_VALUE;
		String bestString = null;
		int bestIndex = -1;
		Location bestLocation = null;
		if (this.showing) {
			Location first = this.location.clone().add(0,
					(this.lines.size() / 2) * distance, 0);
			double r;
			for (int i = 0; i < this.lines.size(); i++) {
				r = MathUtils.getDistanceFromLine(line, eyeLocation, first);
				if (r < bestDistance) {
					bestDistance = r;
					bestString = this.lines.get(i);
					bestIndex = i;
					bestLocation = first.clone();
				}
				first.subtract(0, distance, 0);
			}
		} else {
			return null;
		}
		return new HolographicMenuChoice(bestLocation, bestDistance,
				bestString, this, bestIndex);
	}

	public void show(Location loc) {
		show(loc, this.player);
	}

	public void show(Location loc, long ticks) {
		show(loc);
		new BukkitRunnable() {
			@Override
			public void run() {
				destroy();
			}
		}.runTaskLater(this.plugin, ticks);
	}

	public void show(Location loc, Player player) {
		ServerUtils.verbose(loc);
		if (this.showing == true) {
			try {
				throw new Exception("Is already showing!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.player = player;
		Location first = loc.clone().add(0, (this.lines.size() / 2) * distance,
				0);
		for (int i = 0; i < this.lines.size(); i++) {
			this.ids.addAll(showLine(first.clone(), this.lines.get(i), player));
			first.subtract(0, distance, 0);
		}
		this.showing = true;
		this.location = loc;
	}

}
