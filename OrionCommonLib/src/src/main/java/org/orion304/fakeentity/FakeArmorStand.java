package src.main.java.org.orion304.fakeentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_9_R1.EntityArmorStand;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_9_R1.PacketPlayOutSpawnEntityLiving;
import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.utils.CraftMethods;

public class FakeArmorStand implements Runnable, Listener {

	private final EntityArmorStand armorStand;
	private final CraftArmorStand cArmorStand;

	private final OrionPlugin plugin;
	private final World world;
	private final List<Player> spawnToPlayers = new ArrayList<>();
	private Location location;
	private Vector velocity;
	private ItemStack heldItem, boots, chestplate, helmet, leggings;

	private final boolean updateVelocity;
	private boolean isSpawned = false;
	private final int ID;

	private long tick = 0;
	private HashMap<Player, Long> despawnTimers = new HashMap<>();

	private Packet<?> spawnPacket, metadataPacket, destroyPacket, locationPacket, velocityPacket, heldItemPacket,
			bootsPacket, chestplatePacket, helmetPacket, leggingsPacket;
	private List<Packet<?>> equipmentPackets = new ArrayList<>();

	public FakeArmorStand(OrionPlugin plugin, World world, Location location, Vector velocity) {
		this(plugin, world, location, velocity, false, false, true);
	}

	public FakeArmorStand(OrionPlugin plugin, World world, Location location, Vector velocity, boolean visible,
			boolean affectedByGravity, boolean updateVelocity) {
		this.plugin = plugin;
		this.world = world;
		this.updateVelocity = updateVelocity;
		armorStand = new EntityArmorStand(((CraftWorld) world).getHandle());
		cArmorStand = (CraftArmorStand) armorStand.getBukkitEntity();
		cArmorStand.setVisible(visible);
		cArmorStand.setGravity(affectedByGravity);
		cArmorStand.setMarker(true);
		setLocation(location);
		setVelocity(velocity);
		spawnPacket = new PacketPlayOutSpawnEntityLiving(armorStand);
		metadataPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
		destroyPacket = new PacketPlayOutEntityDestroy(armorStand.getId());

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

	public ItemStack getBoots() {
		return boots;
	}

	public ItemStack getChestPlate() {
		return chestplate;
	}

	public ItemStack getHeldItem() {
		return heldItem;
	}

	public ItemStack getHelmet() {
		return helmet;
	}

	public ItemStack getLeggings() {
		return leggings;
	}

	public Location getLocation() {
		return location.clone();
	}

	public Vector getVelocity() {
		return velocity;
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

		updateLocationAndVelocity();
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

	public void setBoots(ItemStack boots) {
		this.boots = boots;
		bootsPacket = boots == null ? null
				: new PacketPlayOutEntityEquipment(armorStand.getId(), 1, CraftItemStack.asNMSCopy(boots));
		updateEquipmentPackets();
	}

	public void setChestPlate(ItemStack chestplate) {
		this.chestplate = chestplate;
		bootsPacket = chestplate == null ? null
				: new PacketPlayOutEntityEquipment(armorStand.getId(), 3, CraftItemStack.asNMSCopy(chestplate));
		updateEquipmentPackets();
	}

	public void setHeldItem(ItemStack heldItem) {
		this.heldItem = heldItem;
		bootsPacket = heldItem == null ? null
				: new PacketPlayOutEntityEquipment(armorStand.getId(), 0, CraftItemStack.asNMSCopy(heldItem));
		updateEquipmentPackets();
	}

	public void setHelmet(ItemStack helmet) {
		this.helmet = helmet;
		bootsPacket = helmet == null ? null
				: new PacketPlayOutEntityEquipment(armorStand.getId(), 4, CraftItemStack.asNMSCopy(helmet));
		updateEquipmentPackets();
	}

	public void setLeggings(ItemStack leggings) {
		this.leggings = leggings;
		bootsPacket = leggings == null ? null
				: new PacketPlayOutEntityEquipment(armorStand.getId(), 2, CraftItemStack.asNMSCopy(leggings));
		updateEquipmentPackets();
	}

	public void setLocation(Location location) {
		this.location = location.clone();
		cArmorStand.teleport(location);
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
		cArmorStand.setVelocity(velocity);
	}

	public void spawn() {
		isSpawned = true;
		for (Player player : world.getPlayers()) {
			spawnTo(player);
		}
	}

	public void spawnTo(Player player) {
		spawnToPlayers.add(player);
		CraftMethods.sendPacket(plugin, player, spawnPacket, metadataPacket);
		CraftMethods.sendPacket(plugin, player, equipmentPackets);
	}

	private void updateEquipment() {

		List<Player> toRemove = new ArrayList<>();
		for (Player player : spawnToPlayers) {
			if (player.isDead() || !player.isOnline()) {
				toRemove.add(player);
				continue;
			}
			CraftMethods.sendPacket(plugin, player, equipmentPackets);
		}
		spawnToPlayers.removeAll(toRemove);
	}

	private void updateEquipmentPackets() {
		equipmentPackets.clear();
		if (heldItemPacket != null) {
			equipmentPackets.add(heldItemPacket);
		}
		if (bootsPacket != null) {
			equipmentPackets.add(bootsPacket);
		}
		if (chestplatePacket != null) {
			equipmentPackets.add(chestplatePacket);
		}
		if (helmetPacket != null) {
			equipmentPackets.add(helmetPacket);
		}
		if (leggingsPacket != null) {
			equipmentPackets.add(leggingsPacket);
		}
		updateEquipment();
	}

	private void updateLocationAndVelocity() {
		locationPacket = new PacketPlayOutEntityTeleport(armorStand);
		velocityPacket = new PacketPlayOutEntityVelocity(armorStand);
	}

	public void updateMetadata() {
		metadataPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
		for (Player player : spawnToPlayers) {
			CraftMethods.sendPacket(plugin, player, metadataPacket);
		}
	}

}
