package src.main.java.org.orion304;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import src.main.java.org.orion304.holographicmenu.HolographicMenuListener;
import src.main.java.org.orion304.holographicmenu.HolographicMenuSelectThread;
import src.main.java.org.orion304.menu.MenuListener;

public abstract class OrionPlugin extends JavaPlugin {

	public Server server;
	public BukkitScheduler scheduler;
	public PluginManager manager;

	public HolographicMenuListener holographicMenuListener;
	public MenuListener menuListener;

	public void disable() {
		this.scheduler.cancelTasks(this);
	}

	public void enable() {
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
	}

	public HolographicMenuListener getHolographicMenuListener() {
		return this.holographicMenuListener;
	}

	public MenuListener getMenuListener() {
		return this.menuListener;
	}

}
