package src.main.java.org.orion304;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import src.main.java.org.orion304.menu.Menu;
import src.main.java.org.orion304.menu.MenuItem;
import src.main.java.org.orion304.utils.MathUtils;

public abstract class MinigameThread implements Runnable {

	private static final ItemStack voteItem = new ItemStack(Material.DIAMOND, 1);
	private static final ItemStack leaveItem = new ItemStack(
			Material.NETHER_STAR, 1);

	static {
		ItemMeta voteMeta = voteItem.getItemMeta();
		voteMeta.setDisplayName(ChatColor.AQUA + "Vote for...");
		voteItem.setItemMeta(voteMeta);

		ItemMeta leaveMeta = leaveItem.getItemMeta();
		leaveMeta.setDisplayName(ChatColor.RED + "Return to lobby");
		leaveItem.setItemMeta(leaveMeta);
	}

	protected GameState state = GameState.OFF;

	final OrionPlugin plugin;
	private final int numberOfPlayers;

	final int playerThreshold;
	private long fireworkTick = 0;

	public long starttime;
	private final long prepDuration, celebrationsDuration;
	private final List<Player> fireworkPlayers = new ArrayList<>();
	private final List<ArenaLocation> arenas = new ArrayList<>();
	public ArenaLocation currentArena;
	private Menu voteMenu;

	public MinigameThread(OrionPlugin plugin, int minNumberOfPlayers,
			int numberOfPlayers, long prepDuration, long celebrationsDuration) {
		this.plugin = plugin;
		this.playerThreshold = minNumberOfPlayers;
		this.numberOfPlayers = numberOfPlayers;
		this.prepDuration = prepDuration;
		this.celebrationsDuration = celebrationsDuration;

		MinigameThreadListener listener = new MinigameThreadListener(this);
		Bukkit.getPluginManager().registerEvents(listener, this.plugin);
	}

	/**
	 * Not called by the template, must be called by a subclass. Begins the
	 * celebrations.
	 */
	abstract protected void beginCelebrations();

	/**
	 * Called by this template when preparations are ready to begin.
	 */
	abstract protected void beginPreparations();

	/**
	 * Called by this template when the game is ready to run.
	 */
	abstract protected void beginRunning();

	/**
	 * Called by this template every loop to handle celebrations.
	 */
	abstract protected void celebrations();

	/**
	 * Called by this template every loop to check if the game is over and ready
	 * to begin celebrations.
	 */
	abstract protected void checkForCelebrationsConditions();

	/**
	 * Called by this template every loop to check if the celebrations are
	 * finished.
	 */
	protected void checkForOffConditions() {
		if (System.currentTimeMillis() > this.starttime
				+ this.celebrationsDuration) {
			gotoOff();
		}
	}

	/**
	 * Called by this template every loop to check if there are enough players
	 * online to begin preparations.
	 */
	protected void checkForPrepConditions() {
		if (Bukkit.getOnlinePlayers().length >= this.playerThreshold) {
			gotoPrep();
		}
	}

	/**
	 * Called by this template every loop to check if preparation is complete
	 * and begins running the game.
	 */
	protected void checkForRunConditions() {
		long time = System.currentTimeMillis();
		if (time > this.starttime + this.prepDuration) {
			this.state = GameState.RUNNING;
			chooseWinningArenaLocation();
			beginRunning();
		}
	}

	/**
	 * Not called by this template. Selects the players to shoot fireworks on
	 * when the game is complete.
	 * 
	 * @param players
	 *            The players to celebrate.
	 */
	protected void chooseFireworkPlayers(Collection<Player> players) {
		this.fireworkPlayers.clear();
		this.fireworkPlayers.addAll(players);
	}

	/**
	 * Not called by this template. Selects the players to shoot fireworks on
	 * when the game is complete.
	 * 
	 * @param players
	 *            The players to celebrate.
	 */
	protected void chooseFireworkPlayers(Player... players) {
		chooseFireworkPlayers(Arrays.asList(players));
	}

	/**
	 * Called by this template when the game switches to the running state.
	 * Changes this.currentArena to reflect the arena which won the vote phase.
	 */
	protected void chooseWinningArenaLocation() {
		List<ArenaLocation> choices = new ArrayList<>();
		for (ArenaLocation arena : this.arenas) {
			for (int i = 0; i < arena.getVotes(); i++) {
				choices.add(arena);
			}
		}

		if (choices.isEmpty()) {
			choices.addAll(this.arenas);
		}

		this.currentArena = MathUtils.randomChoiceFromCollection(choices);
	}

	/**
	 * Called by this template when the game ends and clears this.currentArena.
	 */
	protected void clearCurrentArena() {
		this.currentArena = null;
		this.arenas.clear();
	}

	abstract protected void complete();

	/**
	 * Called by this template when the game switches to the off state.
	 */
	abstract protected void end();

	/**
	 * Fires a random firework at the location.
	 * 
	 * @param location
	 */
	private void fireFirework(Location location) {
		Firework fw = (Firework) location.getWorld().spawnEntity(location,
				EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		// Our random generator
		Random r = new Random();

		// Get the type
		int rt = r.nextInt(5) + 1;
		Type type = Type.BALL;
		if (rt == 1) {
			type = Type.BALL;
		}
		if (rt == 2) {
			type = Type.BALL_LARGE;
		}
		if (rt == 3) {
			type = Type.BURST;
		}
		if (rt == 4) {
			type = Type.CREEPER;
		}
		if (rt == 5) {
			type = Type.STAR;
		}

		// Get our random colours
		int r1i = r.nextInt(17) + 1;
		int r2i = r.nextInt(17) + 1;
		Color c1 = getColor(r1i);
		Color c2 = getColor(r2i);

		// Create our effect with this
		FireworkEffect effect = FireworkEffect.builder()
				.flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type)
				.trail(r.nextBoolean()).build();

		// Then apply the effect to the meta
		fwm.addEffect(effect);

		// Generate some random power and set it
		int rp = r.nextInt(2) + 1;
		fwm.setPower(rp);

		// Then apply this to our rocket
		fw.setFireworkMeta(fwm);
	}

	/**
	 * Called by this template every loop to fire fireworks for celebration.
	 */
	protected void fireworks() {
		long delta = System.currentTimeMillis() - this.starttime;
		if (delta / 500 > this.fireworkTick) {
			this.fireworkTick = delta / 500;
			Collection<Player> players;
			if (this.fireworkPlayers.isEmpty()) {
				players = this.fireworkPlayers;
			} else {
				players = Arrays.asList(Bukkit.getOnlinePlayers());
			}
			for (Player player : players) {
				fireFirework(player.getLocation());
			}
		}
	}

	/**
	 * Returns the list of arena that are chosen for voting.
	 * 
	 * @return
	 */
	public List<? extends ArenaLocation> getChosenArenas() {
		return this.arenas;
	}

	/**
	 * Returns a color based on the integer parameter
	 * 
	 * @param i
	 *            The integer
	 * @return The chat color
	 */
	private Color getColor(int i) {
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}

		return c;
	}

	/**
	 * Return the state the game is in.
	 * 
	 * @return The GameState.
	 */
	public GameState getState() {
		return this.state;
	}

	/**
	 * Not called by this template. Gives an item for leaving to the specified
	 * player.
	 * 
	 * @param player
	 *            The player to give the leave item to.
	 */
	public void giveLeaveItem(Player player) {
		Inventory inventory = player.getInventory();
		inventory.setItem(8, leaveItem);
	}

	/**
	 * Not called by this template. Gives an item for voting to the specified
	 * player.
	 * 
	 * @param player
	 *            The player to give the vote item to.
	 */
	public void giveVoteItem(Player player) {
		Inventory inventory = player.getInventory();
		inventory.setItem(0, voteItem);
	}

	/**
	 * Called by this template to switch to the off state.
	 */
	public void gotoOff() {
		this.state = GameState.OVER;
		end();
		clearCurrentArena();
	}

	/**
	 * Called by this template to switch to the prep state.
	 */
	public void gotoPrep() {
		this.state = GameState.PREP;
		beginPreparations();
		this.starttime = System.currentTimeMillis();
		this.plugin.getCustomPlayerHandler().addGlobalCountdown(
				this.prepDuration);
	}

	/**
	 * Called by this template when a player interacts with an item in their
	 * inventory. Handles the voting of the player or calls handleItemInteract.
	 * 
	 * @param player
	 *            The player who interacted with an item.
	 * @param item
	 *            The item.
	 * @return True if the event needs to be cancelled.
	 */
	public boolean handleInteract(Player player, ItemStack item) {
		if (voteItem.equals(item)) {
			this.voteMenu.openMenu(player);
			return true;
		}
		if (leaveItem.equals(item)) {
			Bungee.disconnect(player);
			return true;
		}
		return handleItemInteract(player, item);
	}

	/**
	 * Called by this template interacts with an item that isn't used for map
	 * voting.
	 * 
	 * @param player
	 *            The player who interacted with the item.
	 * @param item
	 *            The item.
	 * @return True if the event needs to be cancelled.
	 */
	abstract protected boolean handleItemInteract(Player player, ItemStack item);

	/**
	 * Called by this template when a player votes on a map.
	 * 
	 * @param player
	 */
	abstract protected void handleVote(Player player);

	/**
	 * Not called by this template. This installs the selected arenas into the
	 * vote item.
	 * 
	 * @param arenas
	 *            The arenas to install.
	 */
	protected void installVotingArenas(List<? extends ArenaLocation> arenas) {
		this.arenas.clear();
		this.voteMenu = new Menu(this.plugin, "Vote For A Map", arenas.size());
		for (int i = 0; i < arenas.size(); i++) {
			ArenaLocation arena = arenas.get(i);
			MenuItem item = new MenuItem(arena.getDisplayName(),
					Material.EMERALD, 1, arena.getLore());
			this.voteMenu.addMenuItem(i, item);
			this.arenas.add(arena);
		}
	}

	/**
	 * Called by this template, and handles when players vote using the vote
	 * menu.
	 * 
	 * @param menu
	 *            The menu that was clicked.
	 * @param i
	 *            The slot that was clicked on.
	 * @param player
	 *            The player who clicked.
	 */
	public void menuClick(Menu menu, int i, Player player) {
		if (menu.equals(this.voteMenu)) {
			if (this.arenas.size() > i) {
				ArenaLocation arena = this.arenas.get(i);
				arena.voteFor();
				player.closeInventory();
				handleVote(player);
			}
		}
	}

	/**
	 * Called by this template every loop to handle the off state.
	 */
	abstract protected void off();

	/**
	 * Called by this template every loop to handle the prep state.
	 */
	abstract protected void prep();

	@Override
	public void run() {
		switch (this.state) {
		case OFF:
			off();
			checkForPrepConditions();
			break;
		case PREP:
			prep();
			checkForRunConditions();
			break;
		case RUNNING:
			running();
			checkForCelebrationsConditions();
			break;
		case CELEBRATIONS:
			celebrations();
			fireworks();
			checkForOffConditions();
			break;
		case OVER:
			complete();
			break;
		}
	}

	/**
	 * Called by this template every loop to handle the running state.
	 */
	abstract protected void running();
}
