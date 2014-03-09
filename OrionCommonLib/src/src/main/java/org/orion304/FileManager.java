package src.main.java.org.orion304;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileManager {

	private FileConfiguration config;
	private final File parent, file;

	/**
	 * Creates a new FilerManager instance, which uses Bukkit FileConfiguration
	 * to store properties.
	 * 
	 * @param file
	 *            The file the properties are stored in.
	 */
	public FileManager(File file) {
		this(file.getParentFile(), file.getName());
	}

	/**
	 * Creates a new FileManager instance, which uses Bukkit FileConfiguration
	 * to store properties.
	 * 
	 * @param path
	 *            The parent directory of the file.
	 * @param filename
	 *            The filename for this configuration file.
	 */
	public FileManager(File path, String filename) {
		this.parent = path;
		checkParentExistence();
		this.file = new File(path, filename);
		load();
	}

	/**
	 * Creates the parent directory, and its parents, if they do not exist.
	 */
	private void checkParentExistence() {
		if (!this.parent.exists()) {
			this.parent.mkdirs();
		}
	}

	/**
	 * Returns all keys in the configuration file.
	 * 
	 * @param deep
	 *            If true, will also return keys of keys, ad infinitum.
	 * @return Set of keys in the configuration file.
	 */
	public Set<String> getKeys(boolean deep) {
		return this.config.getKeys(deep);
	}

	/**
	 * Loads the configuration file into memory. If somehow the parent
	 * directory, its parents, or the file containing the configuration file
	 * don't exist, it creates them and loads a new configuration.
	 */
	public void load() {
		checkParentExistence();
		if (this.file.exists()) {
			try {
				this.config.load(this.file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} else {
			try {
				this.file.createNewFile();
				this.config = new YamlConfiguration();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the object stored by the specified key.
	 * 
	 * @param key
	 *            The specified key in the configuration.
	 * @return The object, its default, or null if no default was specified.
	 */
	public Object loadProperty(String key) {
		return this.config.get(key);
	}

	/**
	 * Returns the object stored by the specified key, or if there is no object
	 * stored, returns the default object specified.
	 * 
	 * @param key
	 *            The specified key in the configuration.
	 * @param defaultObject
	 *            The object to return if there is no object stored in that key.
	 * @return The object stored in the key, or the default object if no object
	 *         is stored in that key, or null if the object stored in that key
	 *         is not of the same class as the default object.
	 */
	@SuppressWarnings("unchecked")
	public <T> T loadProperty(String key, T defaultObject) {
		Object object = this.config.get(key, defaultObject);
		if (defaultObject.getClass().isInstance(object)) {
			return (T) object;
		}
		return null;
	}

	/**
	 * Saves the configuration to the file, creating the parent directories if
	 * they do not exist.
	 */
	public void save() {
		checkParentExistence();
		try {
			this.config.save(this.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the defaults for this configuration file.
	 * 
	 * @param defaults
	 *            A map of keys and their objects to store as the defaults for
	 *            the configuration. The objects must be serializable.
	 */
	public void setDefaults(Map<String, Object> defaults) {
		this.config.addDefaults(defaults);
		save();
	}

	/**
	 * Sets the property for the specified key.
	 * 
	 * @param key
	 *            The key to set the property to.
	 * @param property
	 *            The object to set the key to. Must be serializable.
	 */
	public void setProperty(String key, Object property) {
		this.config.set(key, property);
		save();
	}
}
