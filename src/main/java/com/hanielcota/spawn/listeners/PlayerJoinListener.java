package com.hanielcota.spawn.listeners;

import com.hanielcota.spawn.SpawnPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class PlayerJoinListener implements Listener {

    private final SpawnPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getTeleportService().teleportToSpawnWithoutQueue(event.getPlayer());
    }
}