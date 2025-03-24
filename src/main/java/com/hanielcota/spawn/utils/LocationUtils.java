package com.hanielcota.spawn.utils;

import com.hanielcota.spawn.repository.LocationRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Log
@RequiredArgsConstructor
public class LocationUtils {

    private final LocationRepository repository;

    public void saveLocation(Location location) {
        String name = UUID.randomUUID().toString();
        saveLocationByName(name, location);
    }

    public void saveLocationByName(String name, Location location) {
        if (name == null || name.trim().isEmpty() || location == null) {
            log.warning("Parâmetros inválidos para salvar localização.");
            return;
        }
        if (repository.hasLocationByName(name)) {
            log.warning("Localização com nome '" + name + "' já existe.");
            return;
        }

        repository.saveLocation(name, location);
        log.info("Localização '" + name + "' salva com sucesso.");
    }

    public Location getLocationByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.warning("Nome inválido para consulta de localização.");
            return null;
        }
        return repository.getLocationByName(name);
    }

    public void removeLocationByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.warning("Nome inválido para remoção de localização.");
            return;
        }
        repository.removeLocationByName(name);
    }

    public boolean hasLocationByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return repository.hasLocationByName(name);
    }

}