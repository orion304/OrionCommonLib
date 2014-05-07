package src.main.java.org.orion304.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EnvironmentUtils {

	/**
	 * Breaks the block as if it were broken by a player punching it.
	 * 
	 * @param block
	 *            Block to break.
	 */
	public static void breakBlock(Block block) {
		breakBlock(block, Material.AIR);
	}

	/**
	 * Breaks the block as if it were broken by a player holding an item of the
	 * specified type.
	 * 
	 * @param block
	 *            Block to break.
	 * @param type
	 *            Type of item to break it with.
	 */
	public static void breakBlock(Block block, Material type) {
		ItemStack item = new ItemStack(type);
		block.breakNaturally(item);
	}

	/**
	 * Gets the blocks around a location which are contained in a sphere of the
	 * specified radius.
	 * 
	 * @param location
	 *            The center of the sphere of blocks.
	 * @param radius
	 *            The radius of the sphere.
	 * @return The blocks in that sphere.
	 */
	public static Set<Block> getBlocksAroundPoint(Location location,
			double radius) {
		return getBlocksAroundPoint(location, radius, null);
	}

	/**
	 * Gets the blocks of the desired materials around a location which are
	 * contained in a sphere of the specified radius.
	 * 
	 * @param location
	 *            The center of the sphere of blocks.
	 * @param radius
	 *            The radius of the sphere.
	 * @param desired
	 *            A collection of materials to search for.
	 * @return The blocks in that sphere.
	 */
	public static Set<Block> getBlocksAroundPoint(Location location,
			double radius, Collection<Material> desired) {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		int r = (int) radius;

		Set<Block> blocks = new HashSet<Block>();
		for (int dy = -r - 2; dy <= r + 2; dy++) {
			for (int dx = -r - 2; dx <= r + 2; dx++) {
				for (int dz = -r - 2; dz <= r + 2; dz++) {
					if ((double) dx * dx + (double) dy * dy + (double) dz * dz > (double) r
							* r) {
						continue;
					}
					Block block = location.getWorld().getBlockAt(x + dx,
							y + dy, z + dz);
					if (desired != null) {
						if (!desired.contains(block.getType())) {
							continue;
						}
					}
					blocks.add(block);
				}
			}
		}

		return blocks;
	}

	/**
	 * Gets the blocks around a location which are contained in a cube of the
	 * specified length.
	 * 
	 * @param location
	 *            The center of the cube of blocks.
	 * @param length
	 *            The length of the cube.
	 * @return The blocks in that cube.
	 */
	public static Set<Block> getBlocksAroundPointCube(Location location,
			double length) {
		return getBlocksAroundPointCube(location, length, null);
	}

	/**
	 * Gets the blocks of the desired materials around a location which are
	 * contained in a cube of the specified length.
	 * 
	 * @param location
	 *            The center of the cube of blocks.
	 * @param length
	 *            The length of the cube.
	 * @param desired
	 *            A collection of materials to search for.
	 * @return The blocks in that cube.
	 */
	public static Set<Block> getBlocksAroundPointCube(Location location,
			double length, Collection<Material> desired) {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		int r = (int) (length / 2.);

		Set<Block> blocks = new HashSet<Block>();
		for (int dy = -r - 2; dy <= r + 2; dy++) {
			for (int dx = -r - 2; dx <= r + 2; dx++) {
				for (int dz = -r - 2; dz <= r + 2; dz++) {
					Block block = location.getWorld().getBlockAt(x + dx,
							y + dy, z + dz);
					if (desired != null) {
						if (!desired.contains(block.getType())) {
							continue;
						}
					}
					blocks.add(block);
				}
			}
		}

		return blocks;
	}

	/**
	 * Gets the set of blocks that are on the square plane of the specified
	 * length, specified by the center and the plane's normal.
	 * 
	 * @param center
	 *            The center of the plane.
	 * @param normal
	 *            The plane's normal vector (points perpendicular to the plane).
	 * @param length
	 *            The length of the square plane.
	 * @return A set of blocks in the plane.
	 */
	public static Set<Block> getBlocksOnPlane(Location center, Vector normal,
			double length) {
		Vector ortho1 = MathUtils.getOrthogonalVector(normal, 0, 1);
		ortho1 = ortho1.normalize();
		Vector ortho2 = MathUtils.getOrthogonalVector(normal, 90, 1);
		ortho2 = ortho2.normalize();

		int r = (int) (length / 2.0);

		Set<Block> blocks = new HashSet<Block>();

		for (int r1 = -r; r1 <= r; r1++) {
			for (int r2 = -r; r2 <= r; r2++) {
				blocks.add(center.clone().add(ortho1.clone().multiply(r1))
						.add(ortho2.clone().multiply(r2)).getBlock());
			}
		}

		return blocks;
	}

	/**
	 * Returns the set of objects extending the Entity class, specified by c,
	 * around the location in a radius.
	 * 
	 * @param location
	 *            Location to search around.
	 * @param radius
	 *            Radius to search in.
	 * @param c
	 *            Class of entity to search for.
	 * @return List of specified class of entities in the location at a distance
	 *         up to the radius.
	 */
	public static <T extends Entity> Set<T> getEntitiesAroundPoint(
			Location location, double radius, Class<T> c) {
		List<T> list = new ArrayList<>();
		return getEntitiesAroundPoint(location, radius, c, list);
	}

	/**
	 * Returns the set of objects extending the Entity class, specified by c,
	 * around the location in a radius, but excluding entities in avoid.
	 * 
	 * @param location
	 *            Location to search around.
	 * @param radius
	 *            Radius to search in.
	 * @param c
	 *            Class of entity to search for.
	 * @param avoid
	 *            List of entities to ignore in the search.
	 * @return List of specified class of entities in the location at a distance
	 *         up to the radius, excluding those listed in avoid.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Entity> Set<T> getEntitiesAroundPoint(
			Location location, double radius, Class<T> c,
			Collection<? extends T> avoid) {
		World world = location.getWorld();
		List<Entity> entityList = world.getEntities();
		Set<T> entities = new HashSet<>();
		for (Entity entity : entityList) {
			if (c.isInstance(entity)) {
				if (LivingEntity.class.isAssignableFrom(c)
						&& ((LivingEntity) entity).isDead()) {
					continue;
				}
				if (Player.class.isAssignableFrom(c)
						&& !((Player) entity).isOnline()) {
					continue;
				}
				if (!avoid.contains(entity)) {
					if (location.distance(entity.getLocation()) <= radius) {
						entities.add((T) entity);
					}
				}
			}
		}
		return entities;
	}

	/**
	 * Returns the set of entities around the location in a radius, but
	 * excluding entities in avoid.
	 * 
	 * @param location
	 *            Location to search around.
	 * @param radius
	 *            Radius to search in. Class of entity to search for.
	 * @param avoid
	 *            List of entities to ignore in the search.
	 * @return List of entities in the location at a distance up to the radius,
	 *         excluding those listed in avoid.
	 */
	public static Set<Entity> getEntitiesAroundPoint(Location location,
			double radius, Collection<? extends Entity> avoid) {
		return getEntitiesAroundPoint(location, radius, Entity.class, avoid);
	}

	/**
	 * Returns the set of entities around the location in a radius.
	 * 
	 * @param location
	 *            Location to search around.
	 * @param radius
	 *            Radius to search in.
	 * @return List of entities in the location at a distance up to the radius.
	 */
	public static Set<Entity> getEntitiesAroundPoints(Location location,
			double radius) {
		return getEntitiesAroundPoint(location, radius, Entity.class);
	}

	/**
	 * Returns the a block that is no farther than maxdistance away from the
	 * given location that a player can teleport to without suffocating or
	 * falling.
	 * 
	 * @param location
	 *            The location to search.
	 * @param maxdistance
	 *            The max distance from the location to search (vertically).
	 * @return The block a player can be teleported to, or null if there are
	 *         none.
	 */
	public static Block getFloor(Location location, int maxdistance) {
		Block startblock = location.getBlock();
		Block solidblock = null;
		boolean air = false;
		for (int i = -maxdistance; i < maxdistance; i++) {
			Block block = startblock.getRelative(BlockFace.UP, i);
			if (isTransparent(block)) {
				if (solidblock != null) {
					if (air) {
						return block.getRelative(BlockFace.DOWN);
					}
					air = true;
				} else {
					air = false;
				}
			} else {
				solidblock = block;
				air = false;
			}
		}
		return null;
	}

	/**
	 * Gets the TNT-yield-equivalent for the world based on its difficulty
	 * settings (compensates for how peaceful settings have weaker TNT, etc).
	 * 
	 * @param world
	 *            The world to compensate for.
	 * @param yield
	 *            The desired yield.
	 * @return The effective yield.
	 */
	public static float getTNTYield(World world, float yield) {
		float factor = 1F;
		switch (world.getDifficulty()) {
		case PEACEFUL:
			factor = 2F;
			break;
		case EASY:
			factor = 2F;
			break;
		case NORMAL:
			factor = 1F;
			break;
		case HARD:
			factor = 3F / 4F;
			break;
		}
		return yield * factor;
	}

	/**
	 * Checks if the block is immaterial (i.e. players can stand in it).
	 * 
	 * @param block
	 *            Block to check.
	 * @return True if players can stand in the block, false otherwise.
	 */
	public static boolean isImmaterial(Block block) {
		return !isSolid(block);
	}

	/**
	 * Checks if the block is lava (stationary or otherwise).
	 * 
	 * @param block
	 *            The block to check.
	 * @return True if the block is lava.
	 */
	public static boolean isLava(Block block) {
		return isLava(block.getType());
	}

	/**
	 * Checks if the material is lava (stationary or otherwise).
	 * 
	 * @param type
	 *            The material to check.
	 * @return True if the material is lava.
	 */
	public static boolean isLava(Material type) {
		return type == Material.LAVA || type == Material.STATIONARY_LAVA;
	}

	/**
	 * Checks if the block can be melted naturally.
	 * 
	 * @param block
	 *            The block to check.
	 * @return True if the block can be melted.
	 */
	public static boolean isMeltable(Block block) {
		return isMeltable(block.getType());
	}

	/**
	 * Checks if the material can be melted (if it were a block) naturally.
	 * 
	 * @param type
	 *            The material to check.
	 * @return True if the material can be melted.
	 */
	public static boolean isMeltable(Material type) {
		return type == Material.ICE || type == Material.SNOW;
	}

	/**
	 * Checks if the moon is down in the world (below the horizon).
	 * 
	 * @param world
	 *            The world to check.
	 * @return True if the moon is down in the world.
	 */
	public static boolean isMoonDown(World world) {
		return !isMoonUp(world);
	}

	/**
	 * Checks if the moon is up in the world (above the horizon).
	 * 
	 * @param world
	 *            The world to check.
	 * @return True if the moon is up in the world.
	 */
	public static boolean isMoonUp(World world) {
		if (world.getEnvironment() == Environment.NETHER
				|| world.getEnvironment() == Environment.THE_END) {
			return false;
		}
		long time = world.getTime();
		if (time >= 12950 && time <= 23050) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the block is opaque (Players cannot see through it).
	 * 
	 * @param block
	 *            Block to check.
	 * @return True if players cannot see through the block, false otherwise.
	 */
	public static boolean isOpaque(Block block) {
		return !isTransparent(block);
	}

	/**
	 * Checks if the block is solid (i.e. players cannot stand in it).
	 * 
	 * @param block
	 *            Block to check.
	 * @return True if players cannot stand in the block, false otherwise.
	 */
	public static boolean isSolid(Block block) {
		return block.getType().isSolid();
	}

	/**
	 * Checks if the sun is down in the world (below the horizon).
	 * 
	 * @param world
	 *            The world to check.
	 * @return True if the sun is down in the world.
	 */
	public static boolean isSunDown(World world) {
		return !isSunUp(world);
	}

	/**
	 * Checks if the sun is up in the world (above the horizon).
	 * 
	 * @param world
	 *            The world to check.
	 * @return True if the sun is up in the world.
	 */
	public static boolean isSunUp(World world) {
		long time = world.getTime();
		if (time >= 23500 || time <= 12500) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the block is transparent (Players can see through it).
	 * 
	 * @param block
	 *            Block to check.
	 * @return True if players can see through the block, false otherwise.
	 */
	public static boolean isTransparent(Block block) {
		return block.getType().isTransparent();
	}

	/**
	 * Checks if the block is water (stationary or otherwise).
	 * 
	 * @param block
	 *            The block to check.
	 * @return True if the block is water.
	 */
	public static boolean isWater(Block block) {
		return isWater(block.getType());
	}

	/**
	 * Checks if the material is water (stationary or otherwise).
	 * 
	 * @param type
	 *            The material to check.
	 * @return True if the material is water.
	 */
	public static boolean isWater(Material type) {
		return type == Material.WATER || type == Material.STATIONARY_WATER;
	}

}
