package com.hanielcota.spawn.batch;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface TeleportBatchService {

    void addTeleportRequest(Player player, Location targetLocation, String destinationType);

    void processBatch();

    int getMaxTeleportsPerTick();

    int getQueuePosition(Player player);

}