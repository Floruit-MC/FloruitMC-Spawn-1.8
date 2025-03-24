package com.hanielcota.spawn.tasks;

import com.hanielcota.spawn.batch.TeleportBatchService;
import lombok.AllArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class TeleportBatchTask extends BukkitRunnable {

    private final TeleportBatchService teleportBatchService;

    @Override
    public void run() {
        teleportBatchService.processBatch();
    }
}