package com.hanielcota.spawn.listeners;

import com.hanielcota.spawn.events.PlayerSpawnTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpawnTeleportListener implements Listener {

    @EventHandler
    public void onPlayerSpawnTeleport(PlayerSpawnTeleportEvent event) {
        Player player = event.getPlayer();

        Bukkit.getConsoleSender().sendMessage("§a" + player.getName() + " foi teleportado para o spawn!");
    }
}
