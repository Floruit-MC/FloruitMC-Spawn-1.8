package com.hanielcota.spawn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.hanielcota.spawn.utils.LocationUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("listspawn|spawnlist")
@Description("Lista os pontos de spawn disponíveis")
@AllArgsConstructor
public class ListSpawnCommand extends BaseCommand {

    private LocationUtils locationUtils;

    @Default
    @CommandPermission("spawn.list")
    public void onCommand(Player player, String[] args) {
        boolean hasSpawn = locationUtils.hasLocationByName("spawn");
        boolean hasFallback = locationUtils.hasLocationByName("fallback");

        if (!hasSpawn && !hasFallback) {
            player.sendMessage("§cNenhum spawn está definido no momento!");
            return;
        }

        player.sendMessage("§eSpawns disponíveis:");

        if (hasSpawn) {
            Location spawn = locationUtils.getLocationByName("spawn");
            player.sendMessage("§a- Spawn principal: §7" + formatLocation(spawn));
        }

        if (hasFallback) {
            Location fallback = locationUtils.getLocationByName("fallback");
            player.sendMessage("§a- Spawn de fallback: §7" + formatLocation(fallback));
        }
    }

    private String formatLocation(Location location) {
        return String.format("X: %.1f, Y: %.1f, Z: %.1f (Mundo: %s)",
                location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
    }
}