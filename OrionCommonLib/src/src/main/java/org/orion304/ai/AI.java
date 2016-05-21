package src.main.java.org.orion304.ai;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.bukkit.craftbukkit.v1_9_R1.entity.CraftCreature;
import org.bukkit.entity.Creature;

import net.minecraft.server.v1_9_R1.EntityCreature;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.PathfinderGoal;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.PathfinderGoalTarget;

public class AI {
	private static Field targetSelectorField;
	private static Field goalSelectorField;
	private static Field b;
	private static Field c;

	static {
		try {
			targetSelectorField = EntityInsentient.class
					.getDeclaredField("targetSelector");
			goalSelectorField = EntityInsentient.class
					.getDeclaredField("goalSelector");
			b = PathfinderGoalSelector.class.getDeclaredField("b");
			c = PathfinderGoalSelector.class.getDeclaredField("c");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addAI(EntityCreature creature, int i, PathfinderGoal goal) {
		try {
			goalSelectorField.setAccessible(true);
			PathfinderGoalSelector goalSelector = (PathfinderGoalSelector) goalSelectorField
					.get(creature);
			goalSelector.a(i, goal);
			goalSelectorField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addAI(EntityCreature creature, int i,
			PathfinderGoalTarget goal) {
		try {
			targetSelectorField.setAccessible(true);
			PathfinderGoalSelector targetSelector = (PathfinderGoalSelector) targetSelectorField
					.get(creature);
			targetSelector.a(i, goal);
			targetSelectorField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearAI(Creature creature) {
		clearAI(((CraftCreature) creature).getHandle());
	}

	public static void clearAI(EntityCreature creature) {
		try {
			targetSelectorField.setAccessible(true);
			goalSelectorField.setAccessible(true);
			b.setAccessible(true);
			c.setAccessible(true);

			PathfinderGoalSelector targetSelector = (PathfinderGoalSelector) targetSelectorField
					.get(creature);
			PathfinderGoalSelector goalSelector = (PathfinderGoalSelector) goalSelectorField
					.get(creature);
			b.set(goalSelector, new ArrayList<>());
			c.set(goalSelector, new ArrayList<>());
			b.set(targetSelector, new ArrayList<>());
			c.set(targetSelector, new ArrayList<>());
			targetSelectorField.setAccessible(false);
			goalSelectorField.setAccessible(false);
			b.setAccessible(false);
			c.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
