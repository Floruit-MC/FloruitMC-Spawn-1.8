package com.hanielcota.spawn.cooldown;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class CooldownBuilder {

    private final Cache<UUID, Instant> cooldowns;
    private final Duration duration;
    private final String cooldownName;

    @Builder
    private CooldownBuilder(long duration, TimeUnit timeUnit, String cooldownName) {
        this.duration = Duration.ofMillis(timeUnit != null ? timeUnit.toMillis(duration) : TimeUnit.SECONDS.toMillis(duration));
        this.cooldownName = cooldownName != null ? cooldownName : "default";
        this.cooldowns = Caffeine.newBuilder()
                .expireAfterWrite(this.duration)
                .build();
    }

    public boolean hasCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        return cooldowns.getIfPresent(uuid) != null;
    }

    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), Instant.now());
    }

    public long getRemainingTime(Player player) {
        UUID uuid = player.getUniqueId();
        Instant startTime = cooldowns.getIfPresent(uuid);
        if (startTime == null) {
            return 0;
        }

        Instant expirationTime = startTime.plus(duration);
        Instant now = Instant.now();
        if (now.isAfter(expirationTime)) {
            cooldowns.invalidate(uuid);
            return 0;
        }

        return Duration.between(now, expirationTime).getSeconds();
    }

    public void removeCooldown(Player player) {
        cooldowns.invalidate(player.getUniqueId());
    }
}

