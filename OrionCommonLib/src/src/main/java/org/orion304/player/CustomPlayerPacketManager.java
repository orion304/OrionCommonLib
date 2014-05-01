package src.main.java.org.orion304.player;

import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PlayerConnection;

import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;

public class CustomPlayerPacketManager implements Runnable {

	private final CustomPlayer player;
	private PlayerConnection conn;

	CustomPlayerPacketManager(CustomPlayer player) {
		this.player = player;
	}

	@Override
	public void run() {
		if (this.player.player == null) {
			return;
		}

		if (this.conn == null) {
			this.conn = ((CraftPlayer) this.player.player).getHandle().playerConnection;
		}

		synchronized (this.player.packets) {
			long t = this.player.tick;
			if (this.player.futurePackets.containsKey(t)) {
				this.player.packets.addAll(this.player.futurePackets.get(t));
				this.player.futurePackets.remove(t);
			}

			if (this.player.packets.isEmpty()) {
				return;
			}
			for (Packet packet : this.player.packets) {
				this.conn.sendPacket(packet);
			}
			this.player.clearPackets();
		}
	}

}
