package src.main.java.org.orion304;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.WorldCreator;

public class ArenaLocation {

	private final String key;
	private final String displayName;
	private final String world;
	protected Location location;
	private final List<String> lore = new ArrayList<>();
	private int votes = 0;

	public ArenaLocation(String key, String displayName, String world,
			Location location, Collection<? extends String> lore) {
		this.key = key;
		this.displayName = displayName;
		this.world = world;
		this.location = location;
		this.lore.addAll(lore);
	}

	public ArenaLocation(String key, String displayName, String world,
			Location location, String... lore) {
		this(key, displayName, world, location, Arrays.asList(lore));
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getKey() {
		return this.key;
	}

	public Location getLocation() {
		if (this.location.getWorld() == null) {
			loadWorld();
		}
		return this.location;
	}

	public List<String> getLore() {
		return this.lore;
	}

	public int getVotes() {
		return this.votes;
	}

	public void loadWorld() {
		this.location.setWorld(new WorldCreator(this.world).createWorld());
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void voteFor() {
		this.votes++;
	}

}
