package src.main.java.org.orion304.projectile;

import java.util.Set;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomProjectileHitEntityEvent extends Event implements
		Cancellable {

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean cancelled = false;
	private final CustomProjectile projectile;
	private final LivingEntity closestEntity;
	private final Set<LivingEntity> entities;

	public CustomProjectileHitEntityEvent(CustomProjectile projectile,
			LivingEntity closestEntity, Set<LivingEntity> entities) {
		this.projectile = projectile;
		this.closestEntity = closestEntity;
		this.entities = entities;
	}

	public LivingEntity getClosestEntity() {
		return this.closestEntity;
	}

	public Set<LivingEntity> getEntities() {
		return this.entities;
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
