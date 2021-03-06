package src.main.java.org.orion304.fakeentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityArmorStand;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_9_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_9_R1.PacketPlayOutSpawnEntityLiving;
import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.utils.CraftMethods;

public class FakeBlock implements Runnable, Listener {

	private static final double offset = 0;

	private final EntityArmorStand armorStand;
	private final CraftArmorStand cArmorStand;
	private final Entity block;
	private Vector velocity = new Vector(0, 0, 0);

	private final OrionPlugin plugin;
	private final World world;
	private final List<Player> spawnToPlayers = new ArrayList<>();
	private Location location, armorLocation;

	private boolean isSpawned = false;
	private final int ID;

	private long tick = 0;
	private HashMap<Player, Long> despawnTimers = new HashMap<>();

	private Packet<?> spawnArmorPacket, spawnBlockPacket, attachPacket, metadataPacket, destroyPacket, locationPacket,
			velocityPacket;

	public FakeBlock(OrionPlugin plugin, Location location, Material material) {
		this(plugin, location, material, (byte) 0);
	}

	public FakeBlock(OrionPlugin plugin, Location location, Material material, byte data) {
		this.plugin = plugin;
		this.world = location.getWorld();
		armorStand = new EntityArmorStand(((CraftWorld) world).getHandle());
		cArmorStand = (CraftArmorStand) armorStand.getBukkitEntity();
		cArmorStand.setVisible(false);
		cArmorStand.setGravity(false);
		cArmorStand.setMarker(true);
		cArmorStand.setVelocity(velocity);
		setLocation(location);

		block = CraftMethods.getNewEntity(location);

		spawnArmorPacket = new PacketPlayOutSpawnEntityLiving(armorStand);
		spawnBlockPacket = new PacketPlayOutSpawnEntity(block, 70, material.getId() | (data << 0x10));
		attachPacket = new PacketPlayOutAttachEntity(block, armorStand);
		metadataPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
		destroyPacket = new PacketPlayOutEntityDestroy(armorStand.getId(), block.getId());
		velocityPacket = new PacketPlayOutEntityVelocity(armorStand);

		ID = plugin.scheduler.runTaskTimer(plugin, this, 0L, 1L).getTaskId();
	}

	public void despawn() {
		List<Player> players = new ArrayList<>();
		players.addAll(spawnToPlayers);
		for (Player player : players) {
			despawnTo(player);
		}
	}

	public void despawnTo(Player player) {
		if (spawnToPlayers.contains(player)) {
			spawnToPlayers.remove(player);
			if (player.isOnline() && !player.isDead()) {
				CraftMethods.sendPacket(plugin, player, destroyPacket);
			}
		}
	}

	public void despawnToAfter(long delay, Player player) {
		despawnTimers.put(player, tick + delay);
	}

	public void destroy() {
		plugin.scheduler.cancelTask(ID);
	}

	public ArmorStand getArmorStand() {
		return cArmorStand;
	}

	public Location getLocation() {
		return location.clone();
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		if (isSpawned) {
			Player player = event.getPlayer();
			if (player.getWorld().equals(world)) {
				spawnTo(player);
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (isSpawned) {
			Player player = event.getPlayer();
			if (player.getWorld().equals(world)) {
				spawnTo(player);
			}
		}
	}

	@Override
	public void run() {
		tick++;

		List<Player> despawnPlayers = new ArrayList<>();
		for (Player player : despawnTimers.keySet()) {
			long despawnTick = despawnTimers.get(player);
			if (despawnTick > tick) {
				despawnTo(player);
				despawnPlayers.add(player);
			}
		}

		for (Player player : despawnPlayers) {
			despawnTimers.remove(player);
		}

		updateLocation();
		List<Player> toRemove = new ArrayList<>();
		for (Player player : spawnToPlayers) {
			if (player.isDead() || !player.isOnline()) {
				toRemove.add(player);
				continue;
			}
			CraftMethods.sendPacket(plugin, player, locationPacket, velocityPacket);
		}
		spawnToPlayers.removeAll(toRemove);
	}

	public void setLocation(Location location) {
		this.location = location.clone();
		this.armorLocation = location.clone();
		this.armorLocation.add(0, offset, 0);
		cArmorStand.teleport(armorLocation);
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
		cArmorStand.setVelocity(velocity);
		velocityPacket = new PacketPlayOutEntityVelocity(armorStand);
	}

	public void spawn() {
		isSpawned = true;
		for (Player player : world.getPlayers()) {
			spawnTo(player);
		}
	}

	public void spawnTo(Player player) {
		spawnToPlayers.add(player);
		CraftMethods.sendPacket(plugin, player, spawnArmorPacket, spawnBlockPacket, metadataPacket);
		CraftMethods.sendPacket(plugin, player, attachPacket);
	}

	private void updateLocation() {
		locationPacket = new PacketPlayOutEntityTeleport(armorStand);
	}

	public void updateMetadata() {
		metadataPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
		for (Player player : spawnToPlayers) {
			CraftMethods.sendPacket(plugin, player, metadataPacket);
		}
	}

}
