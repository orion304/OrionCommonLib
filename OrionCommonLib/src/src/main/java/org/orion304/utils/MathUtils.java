package src.main.java.org.orion304.utils;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class MathUtils {

	private static final Random random = new Random();

	/**
	 * Converts a double into a byte representing a fixed point value.
	 * 
	 * @param d
	 *            The double.
	 * @return The fixed point byte.
	 */
	public static byte doubleToFixedPointByte(double d) {
		int ONE = 1 << 5;
		return (byte) (d * ONE);
	}

	/**
	 * Returns the cardinal direction (in the form of a BlockFace) that the
	 * specified vector points towards the most.
	 * 
	 * @param vector
	 *            The vector to check.
	 * @return A BlockFace with the cardinal direction the vector points.
	 */
	public static BlockFace getCardinalDirection(Vector vector) {
		BlockFace[] faces = { BlockFace.NORTH, BlockFace.NORTH_EAST,
				BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH,
				BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
		Vector n, ne, e, se, s, sw, w, nw;
		w = new Vector(-1, 0, 0);
		n = new Vector(0, 0, -1);
		s = n.clone().multiply(-1);
		e = w.clone().multiply(-1);
		ne = n.clone().add(e.clone()).normalize();
		se = s.clone().add(e.clone()).normalize();
		nw = n.clone().add(w.clone()).normalize();
		sw = s.clone().add(w.clone()).normalize();

		Vector[] vectors = { n, ne, e, se, s, sw, w, nw };

		double comp = 0;
		int besti = 0;
		for (int i = 0; i < vectors.length; i++) {
			double dot = vector.dot(vectors[i]);
			if (dot > comp) {
				comp = dot;
				besti = i;
			}
		}

		return faces[besti];
	}

	/**
	 * Gets the distance of the point from a line specified by a vector and a
	 * point on that line.
	 * 
	 * @param line
	 *            The vector showing the direction of the line.
	 * @param pointonline
	 *            Any point on that line.
	 * @param point
	 *            The point of interest to find the distance of.
	 * @return The distance from the point to the line.
	 */
	public static double getDistanceFromLine(Vector line, Location pointonline,
			Location point) {

		Vector AP = new Vector();
		double Ax, Ay, Az;
		Ax = pointonline.getX();
		Ay = pointonline.getY();
		Az = pointonline.getZ();

		double Px, Py, Pz;
		Px = point.getX();
		Py = point.getY();
		Pz = point.getZ();

		AP.setX(Px - Ax);
		AP.setY(Py - Ay);
		AP.setZ(Pz - Az);

		return (AP.crossProduct(line).length()) / (line.length());
	}

	/**
	 * Gets the distance of the point to a line specified by the entity's line
	 * of sight.
	 * 
	 * @param entity
	 *            The entity looking.
	 * @param point
	 *            The point.
	 * @return The distance from the point to the entity's line of sight.
	 */
	public static double getDistanceFromLineOfSight(LivingEntity entity,
			Location point) {
		return getDistanceFromLine(entity.getEyeLocation().getDirection(),
				entity.getEyeLocation(), point);
	}

	/**
	 * Returns the distance vector, pointing from location to destination.
	 * 
	 * @param location
	 *            The base of the vector.
	 * @param destination
	 *            The head of the vector.
	 * @return The distance vector created by the two locations.
	 */
	public static Vector getDistanceVector(Entity location, Entity destination) {
		return getDistanceVector(location.getLocation(),
				destination.getLocation());
	}

	/**
	 * Returns the distance vector, pointing from location to destination.
	 * 
	 * @param location
	 *            The base of the vector.
	 * @param destination
	 *            The head of the vector.
	 * @return The distance vector created by the two locations.
	 */
	public static Vector getDistanceVector(Entity location, Location destination) {
		return getDistanceVector(location.getLocation(), destination);
	}

	/**
	 * Returns the distance vector, pointing from location to destination.
	 * 
	 * @param location
	 *            The base of the vector.
	 * @param destination
	 *            The head of the vector.
	 * @return The distance vector created by the two locations.
	 */
	public static Vector getDistanceVector(Location location, Entity destination) {
		return getDistanceVector(location, destination.getLocation());
	}

	/**
	 * Returns the distance vector, pointing from location to destination.
	 * 
	 * @param location
	 *            The base of the vector.
	 * @param destination
	 *            The head of the vector.
	 * @return The distance vector created by the two locations.
	 */
	public static Vector getDistanceVector(Location location,
			Location destination) {
		double x1, y1, z1;
		double x0, y0, z0;

		x1 = destination.getX();
		y1 = destination.getY();
		z1 = destination.getZ();

		x0 = location.getX();
		y0 = location.getY();
		z0 = location.getZ();

		return new Vector(x1 - x0, y1 - y0, z1 - z0);

	}

	/**
	 * Gets the integer form of the cardinal direction of the BlockFace. For use
	 * in the playEffect method for Effect.SMOKE.
	 * 
	 * @param face
	 *            The BlockFace containing the cardinal direction.
	 * @return The integer representing that cardinal direction.
	 */
	public static int getIntCardinalDirection(BlockFace face) {
		switch (face) {
		case SOUTH:
			return 7;
		case SOUTH_WEST:
			return 6;
		case WEST:
			return 3;
		case NORTH_WEST:
			return 0;
		case NORTH:
			return 1;
		case NORTH_EAST:
			return 2;
		case EAST:
			return 5;
		case SOUTH_EAST:
			return 8;
		default:
			return 4;
		}
	}

	/**
	 * Gets the integer form of the cardinal direction best represented by the
	 * specified vector. For use in the playEffect method for Effect.SMOKE.
	 * 
	 * @param vector
	 *            The vector to find the cardinal direction for.
	 * @return The integer representing that cardinal direction.
	 */
	public static int getIntCardinalDirection(Vector vector) {
		return getIntCardinalDirection(getCardinalDirection(vector));

	}

	/**
	 * Gets the smallest multiple of multipleOf which is larger than the
	 * integer.
	 * 
	 * @param integer
	 *            The integer to check.
	 * @param multipleOf
	 *            The multiple to round to.
	 * @return A multiple of multipleOf.
	 */
	public static int getMultipleOf(int integer, int multipleOf) {
		return multipleOf
				* (int) Math.ceil((double) integer / (double) multipleOf);
	}

	/**
	 * Gets the smallest multiple of nine which is larger than the integer.
	 * 
	 * @param integer
	 *            The integer to check.
	 * @return A multiple of 9.
	 */
	public static int getMultipleOfNine(int integer) {
		return getMultipleOf(integer, 9);
	}

	/**
	 * Returns a vector of the given length which is orthogonal to the specified
	 * axis, specified by the angle in degrees around the plane which is
	 * orthogonal to the axis.
	 * 
	 * @param axis
	 *            The axis to make the vector orthogonal to.
	 * @param degrees
	 *            The degrees on the orthogonal plane that the vector is
	 *            located.
	 * @param length
	 *            The length of the vector.
	 * @return The specified orthogonal vector.
	 */
	public static Vector getOrthogonalVector(Vector axis, double degrees,
			double length) {

		Vector ortho;
		if (axis.getX() == 0 && axis.getY() == 0) {
			ortho = new Vector(1, 0, 0);
		} else {
			ortho = new Vector(axis.getY(), -axis.getX(), 0);
		}
		ortho = ortho.normalize();
		ortho = ortho.multiply(length);

		return rotateVectorAroundVector(axis, ortho, degrees);
	}

	/**
	 * Returns the point on the line created by connecting the origin and the
	 * target, at the specified distance from the origin.
	 * 
	 * @param origin
	 *            The origin of the line.
	 * @param target
	 *            The second point which defines the line and its direction.
	 * @param distance
	 *            The distance away from the origin on that line.
	 * @return The location specified.
	 */
	public static Location getPointOnLine(Location origin, Location target,
			double distance) {
		return origin.clone().add(
				getDistanceVector(origin, target).normalize()
						.multiply(distance));
	}

	/**
	 * Get a random object inside a list.
	 * 
	 * @param The
	 *            list.
	 * @return The random object.
	 */
	public static <T> T randomChoiceFromCollection(List<T> list) {
		if (list.isEmpty()) {
			return null;
		}
		int i = list.size();
		return list.get(random.nextInt(i));
	}

	/**
	 * Returns the vector that would result from rotating the rotator about the
	 * axis by the specified angle in degrees.
	 * 
	 * @param axis
	 *            The axis to rotate the vector around.
	 * @param rotator
	 *            The vector to rotate.
	 * @param degrees
	 *            The number of degrees to rotate it.
	 * @return The resulting rotated vector.
	 */
	public static Vector rotateVectorAroundVector(Vector axis, Vector rotator,
			double degrees) {
		double angle = Math.toRadians(degrees);
		Vector rotation = axis.clone();
		Vector rotate = rotator.clone();
		rotation = rotation.normalize();

		Vector thirdaxis = rotation.crossProduct(rotate).normalize()
				.multiply(rotate.length());

		return rotate.multiply(Math.cos(angle)).add(
				thirdaxis.multiply(Math.sin(angle)));
	}

}
