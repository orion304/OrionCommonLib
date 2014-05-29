package src.main.java.org.orion304;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class Hitbox {

	private final double width, height;
	private Location location;

	public Hitbox(double height, double width) {
		this.height = height;
		this.width = width;
	}

	public Hitbox(LivingEntity entity) {
		this(entity.getEyeHeight() / 2D + .1, .5D);
		setBottomLocation(entity.getLocation());
	}

	public boolean isInside(Hitbox hitbox) {
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
	}

	public void setCenterLocation(Location location) {
		this.location = location;
	}

}
