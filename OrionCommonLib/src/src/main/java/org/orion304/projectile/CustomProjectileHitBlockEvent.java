package src.main.java.org.orion304.projectile;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomProjectileHitBlockEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean cancelled = false;
	private final CustomProjectile projectile;
	private final Block block;

	public CustomProjectileHitBlockEvent(CustomProjectile projectile,
			Block block) {
		this.projectile = projectile;
		this.block = block;
	}

	public Block getBlock() {
		return this.block;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public CustomProjectile getProjectile() {
		return this.projectile;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;

	}

}
