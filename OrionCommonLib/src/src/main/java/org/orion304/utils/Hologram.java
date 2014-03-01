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
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Hologram {
	private static final double distance = 0.23;

	private static List<Integer> showLine(Location loc, String text) {
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
		for (Player player : loc.getWorld().getPlayers()) {
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

	public Hologram(JavaPlugin plugin, String... lines) {
		this.lines.addAll(Arrays.asList(lines));
		this.plugin = plugin;
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
		this.location = null;
	}

	public void show(Location loc) {
		if (this.showing == true) {
			try {
				throw new Exception("Is already showing!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Location first = loc.clone().add(0, (this.lines.size() / 2) * distance,
				0);
		for (int i = 0; i < this.lines.size(); i++) {
			this.ids.addAll(showLine(first.clone(), this.lines.get(i)));
			first.subtract(0, distance, 0);
		}
		this.showing = true;
		this.location = loc;
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

}
