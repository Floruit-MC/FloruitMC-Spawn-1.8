package com.hanielcota.spawn.repository;

import com.hanielcota.spawn.SpawnPlugin;
import com.hanielcota.spawn.serializer.LocationSerializer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository {

    private final SpawnPlugin plugin;
    private final Map<String, Location> locationCache = new ConcurrentHashMap<>();
    private final FileConfiguration config;

    public LocationRepositoryImpl(SpawnPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        loadLocationsToCache();
    }

    @Override
    public void saveLocation(String name, Location location) {
        Map<String, Object> serialized = LocationSerializer.serialize(location);
        config.set("locations." + name, serialized);
        plugin.saveConfig();

        locationCache.put(name, location.clone());
    }

    @Override
    public Location getLocationByName(String name) {
        long startTime = plugin.getPerformanceMetrics().startMeasurement("getLocationByName");
        try {
            Location cached = locationCache.get(name);
            if (cached != null) {
                Bukkit.getConsoleSender().sendMessage("§eLocalização encontrada no cache para " + name);
                return cached;
            }

            ConfigurationSection section = config.getConfigurationSection("locations." + name);
            if (section == null) {
                return null;
            }

            Location location = LocationSerializer.deserialize(plugin, section.getValues(false));
            if (location != null) {
                locationCache.put(name, location);
            }
            return location;
        } finally {
            plugin.getPerformanceMetrics().endMeasurement("getLocationByName", startTime);
        }
    }

    @Override
    public void removeLocationByName(String name) {
        config.set("locations." + name, null);
        plugin.saveConfig();
        locationCache.remove(name);
    }

    @Override
    public boolean hasLocationByName(String name) {
        return locationCache.containsKey(name);
    }

    @Override
    public Map<String, Location> getAllLocations() {
        return new HashMap<>(locationCache);
    }

    private void loadLocationsToCache() {
        ConfigurationSection section = config.getConfigurationSection("locations");
        if (section == null) {
            return;
        }

        section.getKeys(false).forEach(key -> {
            Map<String, Object> data = section.getConfigurationSection(key).getValues(false);
            Location location = LocationSerializer.deserialize(plugin, data);

            if (location != null) {
                locationCache.put(key, location);
            }
        });
    }
}