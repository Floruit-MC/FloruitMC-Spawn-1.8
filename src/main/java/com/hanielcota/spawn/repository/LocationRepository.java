package com.hanielcota.spawn.repository;


import org.bukkit.Location;

import java.util.Map;

public interface LocationRepository {

    void saveLocation(String name, Location location);

    Location getLocationByName(String name);

    void removeLocationByName(String name);

    boolean hasLocationByName(String name);

    Map<String, Location> getAllLocations();

}