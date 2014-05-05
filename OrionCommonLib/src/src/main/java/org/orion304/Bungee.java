package src.main.java.org.orion304;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Bungee {

	static {
		Bukkit.getServer()
				.getMessenger()
				.registerOutgoingPluginChannel(OrionPlugin.getPlugin(),
						"BungeeCord");
	}

	public static void disconnect(Player player) {
		player.sendMessage("[c]Connecting to lobby..");
		Bungee.send(player, "Connect", "lobby" + (new Random().nextInt(3) + 1));
	}

	private static void send(Player player, String... stuff) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			for (String string : stuff) {
				out.writeUTF(string);
			}
		} catch (Exception e) {
			// impossibro
			e.printStackTrace();
		}
		player.sendPluginMessage(OrionPlugin.getPlugin(), "BungeeCord",
				b.toByteArray());
	}

}
