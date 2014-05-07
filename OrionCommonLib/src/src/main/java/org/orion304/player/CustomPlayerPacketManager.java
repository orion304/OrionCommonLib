package src.main.java.org.orion304.player;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
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
				if (packet instanceof PacketPlayOutSpawnEntityLiving) {
					try {
						Field a = packet.getClass().getDeclaredField("a");
						a.setAccessible(true);
						String id = String.valueOf(a.getInt(packet));
						a.setAccessible(false);
						if (!this.player.knownEntities.contains(id)) {
							this.player.knownEntities.add(id);
						}
					} catch (NoSuchFieldException | SecurityException
							| IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				} else if (packet instanceof PacketPlayOutEntityDestroy) {
					try {
						Field a = packet.getClass().getDeclaredField("a");
						a.setAccessible(true);
						int[] ids = (int[]) a.get(packet);
						a.setAccessible(false);
						for (int id : ids) {
							String string = String.valueOf(id);
							if (this.player.knownEntities.contains(string)) {
								this.player.knownEntities.remove(string);
							}
						}
					} catch (NoSuchFieldException | SecurityException
							| IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			this.player.clearPackets();
		}
	}

}
