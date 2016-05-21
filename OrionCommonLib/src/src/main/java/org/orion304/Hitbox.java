package src.main.java.org.orion304;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import src.main.java.org.orion304.projectile.CustomProjectile;
import src.main.java.org.orion304.utils.MathUtils;

public class Hitbox {

	private double width, height;
	private final double width2;
	private final double height2;
	private Location location, cubeCorner;

	public Hitbox(double height, double width) {
		this.height = height;
		this.width = width;
		this.width2 = 2 * width;
		this.height2 = 2 * height;
	}

	public Hitbox(LivingEntity entity) {
		this(entity.getEyeHeight() / 2D + .15, .5D);
		setBottomLocation(entity.getLocation());
	}

	private void cornerCube() {
		this.cubeCorner = this.location.clone().subtract(this.width,
				this.height, this.width);
	}

	public double getHeight() {
		return this.height;
	}

	public Location getLocation() {
		return this.location.clone();
	}

	public double getWidth() {
		return this.width;
	}

	public boolean isInside(Block block) {
		if (block.getWorld() != this.location.getWorld()) {
			return false;
		}
		Hitbox blockBox = new Hitbox(.5, .5);
		blockBox.setCenterLocation(block.getLocation().add(.5, .5, .5));
		return isInside(blockBox);
	}

	public boolean isInside(CustomProjectile projectile) {
		Location location = projectile.getLocation();
		if (location.getWorld() != this.location.getWorld()) {
			return false;
		}
		return MathUtils.linePassThroughCuboid(location,
				projectile.getVelocity(), this.cubeCorner, this.width2,
				this.height2, this.width2);
	}

	public boolean isInside(Hitbox hitbox) {
		if (hitbox.location.getWorld() != this.location.getWorld()) {
			return false;
		}
		double x1 = this.location.getX();
		double x2 = hitbox.location.getX();
		double y1 = this.location.getY();
		double y2 = hitbox.location.getY();
		double z1 = this.location.getZ();
		double z2 = hitbox.location.getZ();

		if (Math.abs(x2 - x1) < this.width + hitbox.width) {
			if (Math.abs(y2 - y1) < this.height + hitbox.height) {
				if (Math.abs(z2 - z1) < this.width + hitbox.width) {
					return true;
				}
			}
		}
		return false;
	}

	public void setBottomLocation(Location location) {
		this.location = location.clone().add(0, this.height, 0);
		cornerCube();
	}

	public void setCenterLocation(Location location) {
		this.location = location;
		cornerCube();
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

}
