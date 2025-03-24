package com.hanielcota.spawn;

import co.aikar.commands.PaperCommandManager;
import com.hanielcota.spawn.batch.TeleportBatchService;
import com.hanielcota.spawn.batch.TeleportBatchServiceImpl;
import com.hanielcota.spawn.commands.*;
import com.hanielcota.spawn.cooldown.CooldownBuilder;
import com.hanielcota.spawn.listeners.PlayerJoinListener;
import com.hanielcota.spawn.listeners.PlayerRespawnListener;
import com.hanielcota.spawn.listeners.SpawnTeleportListener;
import com.hanielcota.spawn.metrics.PerformanceMetrics;
import com.hanielcota.spawn.repository.LocationRepositoryImpl;
import com.hanielcota.spawn.services.TeleportService;
import com.hanielcota.spawn.tasks.TeleportBatchTask;
import com.hanielcota.spawn.utils.ConfigUtil;
import com.hanielcota.spawn.utils.LocationUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class SpawnPlugin extends JavaPlugin {

    @Setter
    private CooldownBuilder cooldown;
    private LocationUtils locationUtils;
    private PaperCommandManager commandManager;
    private TeleportBatchTask teleportBatchTask;
    private ConfigUtil batchConfig;
    private ConfigUtil metricsConfig;
    private ConfigUtil messagesConfig;
    private ConfigUtil cooldownConfig;

    private TeleportService teleportService;

    @Setter
    private TeleportBatchService teleportBatchService;
    @Setter
    private PerformanceMetrics performanceMetrics;

    @Override
    public void onEnable() {
        initializeConfig();
        initializeCommandManager();
        initializeDependencies();
        initializeCooldown();
        loadAndValidateLocations();
        registerCommands();
        registerListeners();
        initializeBatchTask();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new SpawnTeleportListener(), this);
        pluginManager.registerEvents(new PlayerRespawnListener(this), this);
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
    }

    private void initializeConfig() {
        batchConfig = new ConfigUtil(this, "batch.yml");
        batchConfig.createDefaultConfig(this, "batch.yml");

        metricsConfig = new ConfigUtil(this, "metrics.yml");
        metricsConfig.createDefaultConfig(this, "metrics.yml");

        messagesConfig = new ConfigUtil(this, "messages.yml");
        messagesConfig.createDefaultConfig(this, "messages.yml");

        cooldownConfig = new ConfigUtil(this, "cooldown.yml");
        cooldownConfig.createDefaultConfig(this, "cooldown.yml");
    }

    @Override
    public void onDisable() {
        unregisterCommands();
        performanceMetrics.printMetricsReport();
    }

    private void initializeCommandManager() {
        commandManager = new PaperCommandManager(this);
    }

    private void initializeDependencies() {
        try {
            boolean metricsEnabled = getMetricsConfig().getBooleanCached("performance-metrics-enabled", false);
            performanceMetrics = new PerformanceMetrics(metricsEnabled);
            locationUtils = new LocationUtils(new LocationRepositoryImpl(this));

            int maxTeleportsPerTick = batchConfig.getIntCached("max-teleports-per-tick", 5);
            teleportBatchService = new TeleportBatchServiceImpl(maxTeleportsPerTick);

            teleportService = new TeleportService(locationUtils, teleportBatchService);
        } catch (Exception e) {
            getLogger().severe("Erro ao inicializar dependências: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initializeBatchTask() {
        long intervalTicks = batchConfig.getLongCached("batch-interval-ticks", 60L);
        teleportBatchTask = new TeleportBatchTask(teleportBatchService);
        teleportBatchTask.runTaskTimer(this, 0L, intervalTicks);
    }

    private void initializeCooldown() {
        int cooldownSeconds = cooldownConfig.getIntCached("spawn.cooldown-seconds", 10);
        cooldown = CooldownBuilder.builder().duration(cooldownSeconds).timeUnit(TimeUnit.SECONDS).cooldownName("spawn_command").build();
    }

    private void loadAndValidateLocations() {
        Map<String, Location> loadedLocations = locationUtils.getRepository().getAllLocations();
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage("§aLocalizações carregadas: " + loadedLocations.size());

        if (!loadedLocations.containsKey("spawn")) {
            console.sendMessage("§c[SpawnPlugin] Spawn principal não está definido!");
        }
        if (loadedLocations.containsKey("spawn")) {
            console.sendMessage("§b[SpawnPlugin] Spawn principal carregado com sucesso.");
        }

        if (!loadedLocations.containsKey("fallback")) {
            console.sendMessage("§c[SpawnPlugin] Spawn de fallback não está definido!");
        }
        if (loadedLocations.containsKey("fallback")) {
            console.sendMessage("§b[SpawnPlugin] Spawn de fallback carregado com sucesso.");
        }
    }

    private void registerCommands() {
        commandManager.registerCommand(new SetSpawnCommand(locationUtils));
        commandManager.registerCommand(new DeleteSpawnCommand(locationUtils));
        commandManager.registerCommand(new ListSpawnCommand(locationUtils));
        commandManager.registerCommand(new SpawnCommand(locationUtils, teleportBatchService, this));
        commandManager.registerCommand(new ConfigCommand(this));
    }

    private void unregisterCommands() {
        if (commandManager != null) {
            commandManager.unregisterCommands();
        }
    }
}