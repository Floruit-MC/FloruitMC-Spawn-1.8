package com.hanielcota.spawn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.hanielcota.spawn.SpawnPlugin;
import com.hanielcota.spawn.batch.TeleportBatchService;
import com.hanielcota.spawn.events.PlayerSpawnTeleportEvent;
import com.hanielcota.spawn.utils.LocationUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("spawn")
@Description("Teleporta para o spawn principal ou de fallback se permitido")
@AllArgsConstructor
public class SpawnCommand extends BaseCommand {

    private final LocationUtils locationUtils;
    private final TeleportBatchService teleportBatchService;
    private final SpawnPlugin plugin;

    @Default
    public void onCommand(Player player, String[] args) {
        if (plugin.getCooldown().hasCooldown(player)) {
            long remainingTime = plugin.getCooldown().getRemainingTime(player);
            sendConfigMessage(player, "spawn.on_cooldown", "{time}", String.valueOf(remainingTime));
            return;
        }

        String destination = args.length > 0 ? args[0].toLowerCase() : "spawn";

        if (!destination.equals("fallback")) {
            handleTeleport(player, "spawn", "spawn principal");
            return;
        }

        if (!player.hasPermission("spawn.teleport.fallback")) {
            sendConfigMessage(player, "spawn.no_permission_fallback");
            return;
        }

        handleTeleport(player, "fallback", "spawn de fallback");
    }

    private void handleTeleport(Player player, String destination, String displayName) {
        long startTime = plugin.getPerformanceMetrics().startMeasurement("handleTeleport");
        try {
            Location targetLocation = locationUtils.getLocationByName(destination);
            if (targetLocation == null && destination.equals("spawn")) {
                handleFallbackTeleport(player);
                return;
            }

            if (targetLocation == null) {
                sendConfigMessage(player, "spawn.spawn_not_set", "{displayName}", displayName);
                return;
            }

            processTeleport(player, targetLocation, destination, displayName);
        } finally {
            plugin.getPerformanceMetrics().endMeasurement("handleTeleport", startTime);
        }
    }

    private void handleFallbackTeleport(Player player) {
        Location fallbackLocation = locationUtils.getLocationByName("fallback");
        if (fallbackLocation == null) {
            sendConfigMessage(player, "spawn.no_spawns_set");
            return;
        }
        processTeleport(player, fallbackLocation, "fallback", "spawn de fallback");
    }

    private void processTeleport(Player player, Location location, String destinationType, String displayName) {
        PlayerSpawnTeleportEvent event = callEventTeleport(player, location, destinationType);
        if (event.isCancelled()) {
            sendConfigMessage(player, "spawn.teleport_cancelled", "{displayName}", displayName);
            return;
        }

        int currentPosition = teleportBatchService.getQueuePosition(player);
        if (currentPosition != -1) {
            sendConfigMessage(player, "spawn.already_in_queue", "{position}", String.valueOf(currentPosition + 1));
            return;
        }

        try {
            teleportBatchService.addTeleportRequest(player, location, displayName);
            sendConfigMessage(player, "spawn.added_to_queue", "{position}", String.valueOf(teleportBatchService.getQueuePosition(player) + 1));
            plugin.getCooldown().setCooldown(player);
        } catch (Exception e) {
            sendConfigMessage(player, "spawn.error_adding_to_queue");
            plugin.getLogger().warning("Erro ao adicionar teleporte à fila: " + e.getMessage());
        }
    }

    @Subcommand("fila")
    public void onQueuePosition(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendConfigMessage(sender, "spawn.only_players");
            return;
        }

        Player player = (Player) sender;
        int position = plugin.getTeleportBatchService().getQueuePosition(player);
        if (position == -1) {
            sendConfigMessage(player, "spawn.not_in_queue");
            return;
        }

        sendConfigMessage(player, "spawn.queue_position", "{position}", String.valueOf(position + 1));
    }

    private PlayerSpawnTeleportEvent callEventTeleport(Player player, Location location, String destinationType) {
        PlayerSpawnTeleportEvent event = new PlayerSpawnTeleportEvent(player, location, destinationType);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    private void sendConfigMessage(CommandSender sender, String path, String... placeholders) {
        List<String> messageList = plugin.getMessagesConfig().getConfig().getStringList(path);
        if (!messageList.isEmpty()) {
            for (String line : messageList) {
                String formatted = applyPlaceholders(line, placeholders);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted));
            }
            return;
        }

        String message = plugin.getMessagesConfig().getConfig().getString(path);
        if (message != null) {
            String formatted = applyPlaceholders(message, placeholders);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted));
        }
    }

    private String applyPlaceholders(String message, String... placeholders) {
        if (placeholders.length % 2 != 0) return message;
        String result = message;
        for (int i = 0; i < placeholders.length; i += 2) {
            result = result.replace(placeholders[i], placeholders[i + 1]);
        }
        return result;
    }
}