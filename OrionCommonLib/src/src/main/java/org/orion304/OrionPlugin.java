package src.main.java.org.orion304;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import src.main.java.org.orion304.holographicmenu.HolographicMenuListener;
import src.main.java.org.orion304.holographicmenu.HolographicMenuSelectThread;
import src.main.java.org.orion304.menu.MenuListener;
import src.main.java.org.orion304.player.CustomPlayer;
import src.main.java.org.orion304.player.CustomPlayerHandler;

public abstract class OrionPlugin extends JavaPlugin {

	private static OrionPlugin plugin;

	public static OrionPlugin getPlugin() {
		return plugin;
	}

	public Server server;
	public BukkitScheduler scheduler;

	public PluginManager manager;
	public HolographicMenuListener holographicMenuListener;

	public MenuListener menuListener;

	abstract public void disable();

	abstract public void enable();

	public abstract CustomPlayerHandler<? extends CustomPlayer> getCustomPlayerHandler();

	public HolographicMenuListener getHolographicMenuListener() {
		return this.holographicMenuListener;
	}

	public MenuListener getMenuListener() {
		return this.menuListener;
	}

	@Override
	public void onDisable() {
		this.scheduler.cancelTasks(this);
	}

	@Override
	public void onEnable() {
		plugin = this;
		this.server = getServer();
		this.scheduler = this.server.getScheduler();
		this.manager = this.server.getPluginManager();

		HolographicMenuSelectThread thread = new HolographicMenuSelectThread(
				this);
		this.scheduler.scheduleSyncRepeatingTask(this, thread, 0, 10);

		this.menuListener = new MenuListener();
		this.holographicMenuListener = new HolographicMenuListener();

		this.manager.registerEvents(this.holographicMenuListener, this);
		this.manager.registerEvents(this.menuListener, this);
		enable();
	}

}
