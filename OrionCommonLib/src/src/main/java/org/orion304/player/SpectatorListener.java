package src.main.java.org.orion304.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class SpectatorListener implements Listener {

	private final CustomPlayerHandler<? extends CustomPlayer> handler;

	public SpectatorListener(CustomPlayerHandler<? extends CustomPlayer> handler) {
		this.handler = handler;
	}

	private void block(Entity entity, Cancellable event) {
		if (entity instanceof Player) {
			CustomPlayer cPlayer = this.handler
					.getCustomPlayer((Player) entity);
			if (cPlayer.isSpectating()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		block(event.getPlayer(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockDamage(BlockDamageEvent event) {
		block(event.getPlayer(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		block(event.getPlayer(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		block(event.getPlayer(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onDropItem(PlayerDropItemEvent event) {
		block(event.getPlayer(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		block(event.getDamager(), event);
		block(event.getEntity(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event) {
		block(event.getPlayer(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageEvent event) {
		block(event.getEntity(), event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		block(event.getPlayer(), event);
	}
}
