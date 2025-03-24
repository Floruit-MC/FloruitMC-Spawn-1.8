package com.hanielcota.spawn.services;

import com.hanielcota.spawn.batch.TeleportBatchService;
import com.hanielcota.spawn.events.PlayerSpawnTeleportEvent;
import com.hanielcota.spawn.utils.LocationUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TeleportService {

    private final LocationUtils locationUtils;
    private final TeleportBatchService teleportBatchService;

    public void teleportToSpawn(Player player) {
        Location spawnLocation = locationUtils.getLocationByName("spawn");
        if (spawnLocation != null) {
            queueTeleport(player, spawnLocation, "spawn principal");
            return;
        }

        Location fallbackLocation = locationUtils.getLocationByName("fallback");
        if (fallbackLocation != null) {
            queueTeleport(player, fallbackLocation, "spawn de fallback");
            return;
        }

        player.sendMessage("§cO spawn principal e o spawn de fallback não estão definidos!");
    }

    public void teleportToFallback(Player player) {
        Location fallbackLocation = locationUtils.getLocationByName("fallback");
        if (fallbackLocation == null) {
            player.sendMessage("§cO spawn de fallback não está definido!");
            return;
        }

        PlayerSpawnTeleportEvent event = new PlayerSpawnTeleportEvent(player, fallbackLocation, "spawn de fallback");
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.sendMessage("§cO teleporte para o spawn de fallback foi cancelado!");
            return;
        }

        player.teleport(fallbackLocation);

        Location spawnLocation = locationUtils.getLocationByName("spawn");
        if (spawnLocation != null) {
            queueTeleport(player, spawnLocation, "spawn principal");
        }
    }

    public void teleportToSpawnWithoutQueue(Player player) {
        Location spawnLocation = locationUtils.getLocationByName("spawn");
        if (spawnLocation == null) {
            player.sendMessage("§cO spawn principal não está definido!");
            return;
        }

        PlayerSpawnTeleportEvent event = new PlayerSpawnTeleportEvent(player, spawnLocation, "spawn principal");
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.sendMessage("§cO teleporte para o spawn principal foi cancelado!");
            return;
        }

        player.teleport(spawnLocation);
        player.sendMessage("§aTeleportado diretamente para o spawn principal!");
    }

    private void queueTeleport(Player player, Location location, String displayName) {
        PlayerSpawnTeleportEvent event = new PlayerSpawnTeleportEvent(player, location, displayName);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            player.sendMessage("§cO teleporte para o " + displayName + " foi cancelado!");
            return;
        }

        teleportBatchService.addTeleportRequest(player, location, displayName);
        player.sendMessage("§aVocê foi adicionado à fila de teleporte para o " + displayName + "!");
    }
}