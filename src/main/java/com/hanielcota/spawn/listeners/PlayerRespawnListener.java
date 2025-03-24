package com.hanielcota.spawn.listeners;

import com.hanielcota.spawn.SpawnPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

@AllArgsConstructor
public class PlayerRespawnListener implements Listener {

    private final SpawnPlugin plugin;

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(plugin.getLocationUtils().getLocationByName("fallback"));
        plugin.getTeleportService().teleportToFallback(event.getPlayer());
    }
}