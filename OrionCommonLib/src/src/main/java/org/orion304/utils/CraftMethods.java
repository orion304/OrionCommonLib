package src.main.java.org.orion304.utils;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PlayerConnection;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import src.main.java.org.orion304.player.CustomPlayer;

public class CraftMethods {

	public static void sendPacket(CustomPlayer player,
			Collection<Packet> packets) {
		sendPacket(player.getPlayer(), packets);
	}

	public static void sendPacket(CustomPlayer player, Packet... packets) {
		sendPacket(player.getPlayer(), packets);
	}

	public static void sendPacket(Player player, Collection<Packet> packets) {
		PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
		for (Packet packet : packets) {
			conn.sendPacket(packet);
		}
	}

	public static void sendPacket(Player player, Packet... packets) {
		sendPacket(player, Arrays.asList(packets));
	}

	public static void sendPacket(World world, Collection<Packet> packets) {
		for (Player player : world.getPlayers()) {
			sendPacket(player, packets);
		}
	}

	public static void sendPacket(World world, Packet... packets) {
		for (Player player : world.getPlayers()) {
			sendPacket(player, packets);
		}
	}

}
