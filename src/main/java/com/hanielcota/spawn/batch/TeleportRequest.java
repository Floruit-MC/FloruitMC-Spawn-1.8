package com.hanielcota.spawn.batch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class TeleportRequest {

    private final Player player;
    private final Location targetLocation;
    private final String destinationType;

}