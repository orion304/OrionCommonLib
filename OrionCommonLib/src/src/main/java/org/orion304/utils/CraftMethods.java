package src.main.java.org.orion304.utils;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldParticles;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.player.CustomPlayer;

public class CraftMethods {

	public static PacketPlayOutWorldParticles getParticlePacket(String label,
			Location location, float ox, float oy, float oz, float data,
			int number) {
		return new PacketPlayOutWorldParticles(label, (float) location.getX(),
				(float) location.getY(), (float) location.getZ(), ox, oy, oz,
				data, number);
	}

	public static void sendPacket(CustomPlayer player,
			Collection<? extends Packet> packets) {
		player.sendPacket(packets);
	}

	public static void sendPacket(CustomPlayer player, Packet... packets) {
		sendPacket(player, Arrays.asList(packets));
	}

	public static void sendPacket(OrionPlugin plugin, Player player,
			Collection<? extends Packet> packets) {
		CustomPlayer customPlayer = plugin.getCustomPlayerHandler()
				.getCustomPlayer(player);
		sendPacket(customPlayer, packets);
	}

	public static void sendPacket(OrionPlugin plugin, Player player,
			Packet... packets) {
		CustomPlayer customPlayer = plugin.getCustomPlayerHandler()
				.getCustomPlayer(player);
		sendPacket(customPlayer, packets);
	}

	public static void sendPacket(OrionPlugin plugin, World world,
			Collection<? extends Packet> packets) {
		for (Player player : world.getPlayers()) {
			sendPacket(plugin, player, packets);
		}
	}

	public static void sendPacket(OrionPlugin plugin, World world,
			Packet... packets) {
		sendPacket(plugin, world, Arrays.asList(packets));
	}

}
