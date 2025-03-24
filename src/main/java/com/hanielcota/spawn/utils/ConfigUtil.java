package com.hanielcota.spawn.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing YAML configuration files with caching support.
 */
@Getter
public class ConfigUtil {

    private final File file;
    private final FileConfiguration config;
    private final Map<String, Integer> intCache = new HashMap<>(); // Cache para valores inteiros
    private final Map<String, Boolean> booleanCache = new HashMap<>(); // Cache para valores booleanos
    private final Map<String, Long> longCache = new HashMap<>(); // Novo cache para valores long

    /**
     * Constructs a ConfigUtil for a file located in the plugin's data folder.
     *
     * @param plugin the plugin instance
     * @param path   the relative path to the configuration file
     */
    public ConfigUtil(Plugin plugin, String path) {
        this.file = new File(plugin.getDataFolder(), path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * Constructs a ConfigUtil for a file located by its absolute path.
     *
     * @param path the absolute path to the configuration file
     */
    public ConfigUtil(String path) {
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * Creates a default configuration file by saving a resource from the plugin if the file does not exist.
     *
     * @param plugin       the plugin instance
     * @param resourcePath the resource path inside the plugin jar
     */
    public void createDefaultConfig(Plugin plugin, String resourcePath) {
        if (!this.file.exists()) {
            try {
                plugin.saveResource(resourcePath, false);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().severe("Failed to save default config for " + resourcePath + ": " + e.getMessage());
            }
        }
    }

    /**
     * Saves the current configuration to file.
     *
     * @return true if the configuration was saved successfully, false otherwise
     */
    public boolean save() {
        try {
            this.config.save(this.file);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().severe("Error saving file: " + this.file.getName() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Reloads the configuration from file and clears the cache.
     *
     * @return true if the configuration was reloaded successfully, false otherwise
     */
    public boolean reload() {
        try {
            this.config.load(this.file);
            intCache.clear(); // Limpa o cache de inteiros
            booleanCache.clear(); // Limpa o cache de booleanos
            longCache.clear(); // Limpa o cache de longs
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().severe("Error reloading file: " + this.file.getName() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets an integer value from the configuration, using cache if available.
     *
     * @param path         the path to the integer value in the config
     * @param defaultValue the default value if not found in config or cache
     * @return the integer value
     */
    public int getIntCached(String path, int defaultValue) {
        if (intCache.containsKey(path)) {
            return intCache.get(path);
        }

        int value = config.getInt(path, defaultValue);
        intCache.put(path, value);
        return value;
    }

    /**
     * Gets a boolean value from the configuration, using cache if available.
     *
     * @param path         the path to the boolean value in the config
     * @param defaultValue the default value if not found in config or cache
     * @return the boolean value
     */
    public boolean getBooleanCached(String path, boolean defaultValue) {
        if (booleanCache.containsKey(path)) {
            return booleanCache.get(path);
        }

        boolean value = config.getBoolean(path, defaultValue);
        booleanCache.put(path, value);
        return value;
    }

    /**
     * Gets a long value from the configuration, using cache if available.
     *
     * @param path         the path to the long value in the config
     * @param defaultValue the default value if not found in config or cache
     * @return the long value
     */
    public long getLongCached(String path, long defaultValue) {
        if (longCache.containsKey(path)) {
            return longCache.get(path);
        }

        long value = config.getLong(path, defaultValue);
        longCache.put(path, value);
        return value;
    }
}