package src.main.java.org.orion304.utils;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityItem;
import net.minecraft.server.v1_9_R1.EnumParticle;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketPlayOutWorldEvent;
import net.minecraft.server.v1_9_R1.PacketPlayOutWorldParticles;
import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.player.CustomPlayer;

public class CraftMethods {

	public static net.minecraft.server.v1_9_R1.World getCraftWorld(org.bukkit.World world) {
		return ((CraftWorld) world).getHandle();
	}

	public static Entity getNewEntity(Location location) {
		return new EntityItem(getCraftWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
	}

	public static PacketPlayOutWorldParticles getParticlePacket(EnumParticle type, boolean bool, Location location,
			float ox, float oy, float oz, float data, int number, int... varargs) {
		return new PacketPlayOutWorldParticles(type, bool, (float) location.getX(), (float) location.getY(),
				(float) location.getZ(), ox, oy, oz, data, number, varargs);
	}

	public static PacketPlayOutWorldEvent getWorldEvent(int id, Location location, int data) {
		return new PacketPlayOutWorldEvent(id,
				new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), data, false);
	}

	public static void sendPacket(CustomPlayer<? extends OrionPlugin> player, Collection<? extends Packet> packets) {
		player.sendPacket(packets);
	}

	public static void sendPacket(CustomPlayer<? extends OrionPlugin> player, Packet... packets) {
		sendPacket(player, Arrays.asList(packets));
	}

	public static void sendPacket(OrionPlugin plugin, Player player, Collection<? extends Packet> packets) {
		CustomPlayer<? extends OrionPlugin> customPlayer = plugin.getCustomPlayerHandler().getCustomPlayer(player);
		sendPacket(customPlayer, packets);
	}

	public static void sendPacket(OrionPlugin plugin, Player player, Packet... packets) {
		CustomPlayer<? extends OrionPlugin> customPlayer = plugin.getCustomPlayerHandler().getCustomPlayer(player);
		sendPacket(customPlayer, packets);
	}

	public static void sendPacket(OrionPlugin plugin, World world, Collection<? extends Packet> packets) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().equals(world)) {
				sendPacket(plugin, player, packets);
			}
		}
	}

	public static void sendPacket(OrionPlugin plugin, World world, Packet... packets) {
		sendPacket(plugin, world, Arrays.asList(packets));
	}

}
