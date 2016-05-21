package src.main.java.org.orion304;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_9_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_9_R1.PacketPlayInClientCommand.EnumClientCommand;

public class CommonListener implements Listener {

	private final JavaPlugin plugin;

	public CommonListener(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		event.setCancelled(!(event.getPlayer().isOp() && event.getPlayer()
				.getGameMode() == GameMode.CREATIVE));
	}

	@EventHandler
	public void onBlockFade(BlockFadeEvent event) {
		// event.setCancelled(true);
	}

	@EventHandler
	public void onBlockFlow(BlockFromToEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.getCause() == IgniteCause.LIGHTNING
				|| event.getCause() == IgniteCause.SPREAD) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		event.setCancelled(!(event.getPlayer().isOp() && event.getPlayer()
				.getGameMode() == GameMode.CREATIVE));
		if (event.isCancelled()) {
			event.getPlayer().updateInventory();
		}
	}

	@EventHandler
	public void onEntityCombustEvent(EntityCombustEvent event) {
		if (event.getDuration() == 8 && !(event.getEntity() instanceof Player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		event.blockList().clear();
	}

	@EventHandler
	public void onHangingBreak(HangingBreakEvent event) {
		if (event.getCause() == RemoveCause.ENTITY) {
			HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent) event;
			if (entityEvent.getRemover() instanceof Player) {
				Player remover = (Player) entityEvent.getRemover();
				if (remover.isOp()
						&& remover.getGameMode() == GameMode.CREATIVE) {
					return;
				}
			}
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
		event.setFoodLevel(20);
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			player.setSaturation(200);
		}
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		event.setCancelled(true);
		event.getPlayer().updateInventory();
	}

	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		event.setCancelled(true);
		event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				((CraftPlayer) event.getEntity()).getHandle().playerConnection
						.a(new PacketPlayInClientCommand(
								EnumClientCommand.PERFORM_RESPAWN));
			}
		}.runTaskLater(this.plugin, 2L);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		event.setLeaveMessage(null);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

}
