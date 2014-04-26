package src.main.java.org.orion304;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;

public class ArenaLocation {

	private final String key;
	private final String displayName;
	private final Location location;
	private final List<String> lore = new ArrayList<>();
	private int votes = 0;

	public ArenaLocation(String key, String displayName, Location location,
			Collection<? extends String> lore) {
		this.key = key;
		this.displayName = displayName;
		this.location = location;
		this.lore.addAll(lore);
	}

	public ArenaLocation(String key, String displayName, Location location,
			String... lore) {
		this(key, displayName, location, Arrays.asList(lore));
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getKey() {
		return this.key;
	}

	public Location getLocation() {
		return this.location;
	}

	public List<String> getLore() {
		return this.lore;
	}

	public int getVotes() {
		return this.votes;
	}

	public void voteFor() {
		this.votes++;
	}

}
