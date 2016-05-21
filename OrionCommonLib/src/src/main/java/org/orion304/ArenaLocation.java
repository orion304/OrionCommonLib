package src.main.java.org.orion304;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.WorldCreator;

public class ArenaLocation {

	private String key;
	private String displayName;
	private String world;
	protected Location location;

	private List<String> lore = new ArrayList<>();

	private int votes = 0;

	public ArenaLocation(String key, String displayName, String world, Location location,
			Collection<? extends String> lore) {
		this.key = key;
		this.displayName = displayName;
		this.world = world;
		this.location = location;
		this.lore.addAll(lore);
	}

	public ArenaLocation(String key, String displayName, String world, Location location, String... lore) {
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

	public String getWorld() {
		return world;
	}

	public void loadWorld() {
		this.location.setWorld(new WorldCreator(this.world).createWorld());
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public void voteFor() {
		this.votes++;
	}

}
