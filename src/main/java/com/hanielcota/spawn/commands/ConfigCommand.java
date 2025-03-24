package com.hanielcota.spawn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.hanielcota.spawn.SpawnPlugin;
import com.hanielcota.spawn.batch.TeleportBatchServiceImpl;
import com.hanielcota.spawn.cooldown.CooldownBuilder;
import com.hanielcota.spawn.metrics.PerformanceMetrics;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandAlias("spawnconfig")
@RequiredArgsConstructor
public class ConfigCommand extends BaseCommand {

    private final SpawnPlugin plugin;

    @Default
    @CommandPermission("spawn.reload")
    public void onReload(CommandSender sender) {
        if (!plugin.getMetricsConfig().reload()) {
            sendConfigMessage(sender, "config.metrics_reload_failed");
            return;
        }
        boolean metricsEnabled = plugin.getMetricsConfig().getBooleanCached("performance-metrics-enabled", false);
        plugin.setPerformanceMetrics(new PerformanceMetrics(metricsEnabled));

        if (!plugin.getBatchConfig().reload()) {
            sendConfigMessage(sender, "config.batch_reload_failed");
            return;
        }
        int maxTeleportsPerTick = plugin.getBatchConfig().getIntCached("max-teleports-per-tick", 5);
        plugin.setTeleportBatchService(new TeleportBatchServiceImpl(maxTeleportsPerTick));

        if (!plugin.getMessagesConfig().reload()) {
            sender.sendMessage("§cFalha ao recarregar a configuração de mensagens (messages.yml)!");
            return;
        }

        if (!plugin.getCooldownConfig().reload()) {
            sender.sendMessage("§cFalha ao recarregar a configuração de cooldown (cooldown.yml)!");
            return;
        }
        int cooldownSeconds = plugin.getCooldownConfig().getIntCached("spawn.cooldown-seconds", 10);
        plugin.setCooldown(CooldownBuilder.builder()
                .duration(cooldownSeconds)
                .timeUnit(TimeUnit.SECONDS)
                .cooldownName("spawn_command")
                .build());

        sendConfigMessage(sender, "config.reload_success",
                "{maxTeleports}", String.valueOf(maxTeleportsPerTick),
                "{metricsStatus}", metricsEnabled ? "Ativadas" : "Desativadas");
    }

    @Subcommand("info")
    @CommandPermission("spawn.batchinfo")
    public void onBatchInfo(CommandSender sender) {
        int configValue = plugin.getBatchConfig().getIntCached("max-teleports-per-tick", 5);
        boolean metricsEnabled = plugin.getMetricsConfig().getBooleanCached("performance-metrics-enabled", false);

        sendConfigMessage(sender, "config.info",
                "{maxTeleports}", String.valueOf(configValue),
                "{metricsStatus}", metricsEnabled ? "Ativadas" : "Desativadas");
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