package src.main.java.org.orion304.projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import src.main.java.org.orion304.OrionPlugin;
import src.main.java.org.orion304.utils.EnvironmentUtils;

public abstract class CustomProjectile implements Runnable {

	private static final Vector g = new Vector(0, -0.03999999910593033D, 0);
	// private static final List<CustomProjectile> projectiles = new
	// ArrayList<>();
	// private static final List<CustomProjectile> toRemove = new ArrayList<>();
	//
	// public static void handle() {
	// toRemove.clear();
	// for (CustomProjectile projectile : projectiles) {
	// projectile.progress();
	// if (!projectile.isAlive) {
	// toRemove.add(projectile);
	// }
	// }
	// projectiles.removeAll(toRemove);
	// }

	protected final OrionPlugin plugin;
	protected final Location location;
	protected final Vector velocity;
	private final LivingEntity shooter;
	private final boolean hasGravity, hitsBlocks, hitsEntities;
	private boolean isAlive = true;
	private final int taskId;

	public CustomProjectile(OrionPlugin plugin, Location location,
			Vector velocity, LivingEntity shooter) {
		this(plugin, location, velocity, shooter, true, true, true);
	}

	public CustomProjectile(OrionPlugin plugin, Location location,
			Vector velocity, LivingEntity shooter, boolean hasGravity,
			boolean hitsBlocks, boolean hitsEntities) {
		this.plugin = plugin;
		this.location = location;
		this.velocity = velocity;
		this.shooter = shooter;
		this.hasGravity = hasGravity;
		this.hitsBlocks = hitsBlocks;
		this.hitsEntities = hitsEntities;
		this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
				this, 0L, 1L);
	}

	abstract protected void animate();

	public Location getLocation() {
		return this.location.clone();
	}

	public LivingEntity getShooter() {
		return this.shooter;
	}

	@Override
	public void run() {
		if (this.hasGravity) {
			this.velocity.add(g);
		}
		this.location.add(this.velocity);
		if (this.hitsBlocks && this.location.getBlock().getType().isSolid()) {
			CustomProjectileHitBlockEvent event = new CustomProjectileHitBlockEvent(
					this, this.location.getBlock());
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				this.isAlive = false;
			}
		} else if (this.hitsEntities) {
			List<LivingEntity> shooter = new ArrayList<>();
			shooter.add(this.shooter);
			Set<LivingEntity> entities = EnvironmentUtils
					.getEntitiesAroundPoint(this.location, 2D,
							LivingEntity.class, shooter);
			double bestDistance = 5, distance;
			LivingEntity closestEntity = null;
			for (LivingEntity entity : entities) {
				distance = entity.getLocation().distance(this.location);
				if (closestEntity == null || bestDistance < distance) {
					bestDistance = distance;
					closestEntity = entity;
				}
			}

			if (!entities.isEmpty()) {
				CustomProjectileHitEntityEvent event = new CustomProjectileHitEntityEvent(
						this, closestEntity, entities);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					this.isAlive = false;
				}
			}
		}

		if (this.isAlive) {
			animate();
		} else {
			Bukkit.getScheduler().cancelTask(this.taskId);
		}
	}
}
