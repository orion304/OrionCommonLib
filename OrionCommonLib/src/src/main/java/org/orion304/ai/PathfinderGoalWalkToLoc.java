package src.main.java.org.orion304.ai;

import org.bukkit.Location;

import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.NavigationAbstract;
import net.minecraft.server.v1_9_R1.PathEntity;
import net.minecraft.server.v1_9_R1.PathfinderGoal;

public class PathfinderGoalWalkToLoc extends PathfinderGoal {
	private final EntityInsentient entity;
	private final NavigationAbstract navigation;
	private PathEntity pathEntity = null;
	private final double speed;

	public PathfinderGoalWalkToLoc(EntityInsentient entity, Location location,
			double speed) {
		this.entity = entity;
		this.navigation = this.entity.getNavigation();
		this.speed = speed;
		this.pathEntity = this.navigation.a(location.getX(), location.getY(),
				location.getZ());
	}

	@Override
	public boolean a() {
		return true;
	}

	@Override
	public void c() {
		if (this.pathEntity != null) {
			this.navigation.a(this.pathEntity, this.speed);
		}
	}
}