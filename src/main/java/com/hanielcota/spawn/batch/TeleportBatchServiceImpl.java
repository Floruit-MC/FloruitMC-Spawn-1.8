package com.hanielcota.spawn.batch;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class TeleportBatchServiceImpl implements TeleportBatchService {

    private final ConcurrentLinkedQueue<TeleportRequest> teleportQueue = new ConcurrentLinkedQueue<>();
    private final Map<Player, Integer> playerPositions = new ConcurrentHashMap<>();
    private final AtomicInteger queueSize = new AtomicInteger(0);
    private final int maxTeleportsPerTick;

    public TeleportBatchServiceImpl(int maxTeleportsPerTick) {
        if (maxTeleportsPerTick <= 0) {
            throw new IllegalArgumentException("maxTeleportsPerTick must be greater than 0");
        }
        this.maxTeleportsPerTick = maxTeleportsPerTick;
    }

    @Override
    public void addTeleportRequest(Player player, Location targetLocation, String destinationType) {
        if (player == null) return;
        if (!player.isOnline()) return;
        if (targetLocation == null) return;

        TeleportRequest request = new TeleportRequest(player, targetLocation, destinationType);
        teleportQueue.add(request);
        playerPositions.put(player, queueSize.getAndIncrement());
    }

    @Override
    public void processBatch() {
        int maxToProcess = Math.min(queueSize.get(), maxTeleportsPerTick);
        for (int i = 0; i < maxToProcess; i++) {
            TeleportRequest request = teleportQueue.poll();
            if (request == null || !request.getPlayer().isOnline()) {
                if (request != null) {
                    queueSize.decrementAndGet();
                    playerPositions.remove(request.getPlayer());
                }
                break;
            }
            processTeleportRequest(request);
        }
    }

    @Override
    public int getQueuePosition(Player player) {
        return playerPositions.getOrDefault(player, -1);
    }

    private void processTeleportRequest(TeleportRequest request) {
        Player player = request.getPlayer();
        queueSize.decrementAndGet();
        playerPositions.remove(player);

        Location location = request.getTargetLocation();
        String destinationType = request.getDestinationType();

        player.teleport(location);
        player.sendMessage("§aTeleportado para o " + destinationType + "!");
    }
}