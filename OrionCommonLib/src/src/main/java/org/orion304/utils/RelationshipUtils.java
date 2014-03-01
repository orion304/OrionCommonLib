package src.main.java.org.orion304.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RelationshipUtils {

	/**
	 * Get the block the Living Entity is looking at, within the specified
	 * range.
	 * 
	 * @param entity
	 *            The Living Entity targeting the block.
	 * @param range
	 *            The maximum range to target.
	 * @return The targeted block.
	 */
	public static Block getTargetedBlock(LivingEntity entity, double range) {
		return getTargetedBlock(entity, range, new ArrayList<Material>());
	}

	/**
	 * Get the block the Living Entity is looking at, within the specified
	 * range, additionally ignoring any blocks of a type listed in transparent.
	 * 
	 * @param entity
	 *            The Living Entity targeting the block.
	 * @param range
	 *            The maximum range to target.
	 * @param transparent
	 *            The collection of materials which should be ignored by the
	 *            targeting algorithm.
	 * @return The targeted block.
	 */
	public static Block getTargetedBlock(LivingEntity entity, double range,
			Collection<Material> transparent) {
		return getTargetedLocation(entity, range, transparent).getBlock();
	}

	/**
	 * Gets the entity, of class c, that is being targeted by the Living Entity,
	 * excluding entities in avoid.
	 * 
	 * @param entity
	 *            The entity doing the targeting.
	 * @param range
	 *            The max range of the targeting.
	 * @param c
	 *            The class of entity that is being targeted.
	 * @param avoid
	 *            A collection of entities which will not be included in the
	 *            targeting.
	 * @return The targeted entity, of class c, that is not in avoid.
	 */
	public static <T extends Entity> T getTargetedEntity(LivingEntity entity,
			double range, Class<T> c, Collection<T> avoid) {
		Location eyeLocation = entity.getEyeLocation();
		Vector direction = eyeLocation.getDirection();
		Set<T> candidates = EnvironmentUtils.getEntitiesAroundPoint(
				eyeLocation, range, c, avoid);
		T result = null;
		double distance, bestdistance = Double.MAX_VALUE;
		for (T target : candidates) {
			Location loc = target.getLocation();
			if (entity instanceof Player && target instanceof Player) {
				if (!((Player) entity).canSee((Player) target)) {
					continue;
				}
			}
			if (!entity.equals(target)
					&& isLineOfSight(loc, eyeLocation)
					&& isInFrontOf(entity, target)
					&& MathUtils.getDistanceFromLine(direction, eyeLocation,
							loc) < 2) {
				distance = loc.distance(eyeLocation);
				if (distance < bestdistance) {
					bestdistance = distance;
					result = target;
				}
			}
		}
		return result;
	}

	/**
	 * Get the location the Living Entity is looking at, within the specified
	 * range.
	 * 
	 * @param entity
	 *            The Living Entity targeting the location.
	 * @param range
	 *            The maximum range to target.
	 * @return The targeted location.
	 */
	public static Location getTargetedLocation(LivingEntity entity, double range) {
		return getTargetedLocation(entity, range, new ArrayList<Material>());
	}

	/**
	 * Get the location the Living Entity is looking at, within the specified
	 * range, additionally ignoring any blocks of a type listed in transparent.
	 * 
	 * @param entity
	 *            The Living Entity targeting the location.
	 * @param range
	 *            The maximum range to target.
	 * @param transparent
	 *            The collection of materials which should be ignored by the
	 *            targeting algorithm.
	 * @return The targeted location.
	 */
	public static Location getTargetedLocation(LivingEntity entity,
			double range, Collection<Material> transparent) {
		Location eyeLocation = entity.getEyeLocation();
		Vector direction = eyeLocation.getDirection();
		direction.normalize();

		Location loc = eyeLocation.clone();
		for (double i = 0; i <= range; i++) {
			loc = eyeLocation.clone().add(direction.clone().multiply(i));
			Block block = loc.getBlock();
			if (transparent.contains(block.getType())) {
				continue;
			}
			if (EnvironmentUtils.isSolid(block)) {
				return loc;
			}
		}
		return loc;
	}

	/**
	 * Checks to see if two blocks are touching each other (they share a face).
	 * 
	 * @param block1
	 *            One of the blocks to check.
	 * @param block2
	 *            The other block to check.
	 * @return True if they share a face, false otherwise.
	 */
	public static boolean isBlockTouching(Block block1, Block block2) {
		for (BlockFace face : new BlockFace[] { BlockFace.NORTH,
				BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP,
				BlockFace.DOWN }) {
			if (block1.getRelative(face).equals(block2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if entity2 is in front of entity1.
	 * 
	 * @param entity1
	 *            The entity to check if they are facing entity2.
	 * @param entity2
	 *            THe entity whose position is checked against entity1's facing
	 *            direction.
	 * @return True if entity1 is looking at entity2 (in other words, entity2 is
	 *         in front of entity1), false otherwise.
	 */
	public static boolean isInFrontOf(LivingEntity entity1, Entity entity2) {
		Location eyeLocation = entity1.getEyeLocation();
		Vector direction = eyeLocation.getDirection();

		Vector distance = entity2.getLocation().toVector()
				.subtract(eyeLocation.toVector());

		return direction.dot(distance) > 0;
	}

	/**
	 * Checks if the two locations have unobstructed line of sight between them.
	 * 
	 * @param location1
	 *            First location.
	 * @param location2
	 *            Second location.
	 * @return True if the line of sight between the locations is unobstructed.
	 */
	public static boolean isLineOfSight(Location location1, Location location2) {
		Vector v1 = location1.toVector();
		Vector v2 = location2.toVector();

		Vector direction = v1.subtract(v2);
		direction.normalize();

		for (double i = 0; i < location1.distance(location2); i++) {
			Location loc = location2.clone().add(direction.clone().multiply(i));
			if (EnvironmentUtils.isOpaque(loc.getBlock())) {
				return false;
			}
		}
		return true;
	}

}
