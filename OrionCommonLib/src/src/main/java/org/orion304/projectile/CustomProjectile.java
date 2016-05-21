package src.main.java.org.orion304.projectile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import src.main.java.org.orion304.Hitbox;
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
	protected Location location;
	protected Vector velocity;
	protected LivingEntity shooter;
	protected boolean hasGravity;

	private final boolean hitsBlocks;

	private final boolean hitsEntities;
	protected boolean isAlive = true;
	private final int taskId;
	private final double hitRadius;
	protected Hitbox hitbox;
	private final Hitbox bigHitbox;
	protected long tick = 0L;

	protected List<Entity> nearbyEntities = new ArrayList<>();
	protected List<Block> nearbyBlocks = new ArrayList<>();

	public CustomProjectile(OrionPlugin plugin, Location location,
			Vector velocity, LivingEntity shooter) {
		this(plugin, location, velocity, shooter, true, true, true);
	}

	public CustomProjectile(OrionPlugin plugin, Location location,
			Vector velocity, LivingEntity shooter, boolean hasGravity,
			boolean hitsBlocks, boolean hitsEntities) {
		this(plugin, location, velocity, shooter, hasGravity, hitsBlocks,
				hitsEntities, .1D);
	}

	public CustomProjectile(OrionPlugin plugin, Location location,
			Vector velocity, LivingEntity shooter, boolean hasGravity,
			boolean hitsBlocks, boolean hitsEntities, double hitRadius) {
		this.plugin = plugin;
		this.location = location;
		this.velocity = velocity;
		this.shooter = shooter;
		this.hasGravity = hasGravity;
		this.hitsBlocks = hitsBlocks;
		this.hitsEntities = hitsEntities;
		this.hitRadius = hitRadius;
		this.hitbox = new Hitbox(hitRadius, hitRadius);
		this.bigHitbox = new Hitbox(velocity.lengthSquared(),
				velocity.lengthSquared());
		this.hitbox.setCenterLocation(location);
		this.bigHitbox.setCenterLocation(location);
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

	public Vector getVelocity() {
		return this.velocity.clone();
	}

	@Override
	public void run() {
		this.tick += 1L;
		if (this.isAlive) {
			int i = (int) (this.velocity.length() * 4.0D);
			if (i < 1) {
				i = 1;
			}
			double length = this.velocity.length();
			this.bigHitbox.setHeight(length);
			this.bigHitbox.setWidth(length);
			this.bigHitbox.setCenterLocation(this.location.clone().add(
					this.velocity.clone().multiply(0.5D)));

			this.nearbyEntities.clear();
			this.nearbyEntities.addAll(EnvironmentUtils
					.getEntitiesInHitbox(this.bigHitbox));
			this.nearbyBlocks.clear();
			this.nearbyBlocks.addAll(EnvironmentUtils
					.getBlocksInHitbox(this.bigHitbox));

			Map<LivingEntity, Hitbox> hitboxes = new HashMap<>();
			for (Entity entity : this.nearbyEntities) {
				if (((entity instanceof LivingEntity))
						&& (!entity.equals(this.shooter))) {
					LivingEntity lE = (LivingEntity) entity;
					Hitbox hitbox = new Hitbox(lE);
					hitboxes.put(lE, hitbox);
				}
			}
			for (int j = 0; j < i; j++) {
				Vector v = this.velocity.clone().multiply(1.0D / i);
				if (this.hasGravity) {
					this.velocity.add(g.clone().multiply(1.0D / i));
				}
				if (this.location.getY() + v.getY() >= this.location.getWorld()
						.getMaxHeight()) {
					this.isAlive = false;
				} else {
					this.location.add(v);
					this.hitbox.setCenterLocation(this.location);
					if ((this.hitsBlocks)
							&& (this.location.getBlock().getType().isSolid())) {
						CustomProjectileHitBlockEvent event = new CustomProjectileHitBlockEvent(
								this, this.location.getBlock());
						Bukkit.getPluginManager().callEvent(event);
						if (!event.isCancelled()) {
							this.isAlive = false;
						}
					} else if (this.hitsEntities) {
						Set<LivingEntity> affectedEntities = new HashSet<>();
						double bestDistance = 5.0D;
						LivingEntity closestEntity = null;
						for (LivingEntity entity : hitboxes.keySet()) {
							if (hitboxes.get(entity).isInside(this.hitbox)) {
								affectedEntities.add(entity);
								double distance = entity.getLocation()
										.distanceSquared(this.location);
								if ((closestEntity == null)
										|| (bestDistance < distance)) {
									bestDistance = distance;
									closestEntity = entity;
								}
							}
						}
						if (!affectedEntities.isEmpty()) {
							CustomProjectileHitEntityEvent event = new CustomProjectileHitEntityEvent(
									this, closestEntity, affectedEntities);
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCancelled()) {
								this.isAlive = false;
							}
						}
					}
				}
				if (this.isAlive) {
					animate();
				} else {
					Bukkit.getScheduler().cancelTask(this.taskId);
					return;
				}
			}
		} else {
			Bukkit.getScheduler().cancelTask(this.taskId);
		}
	}
}
