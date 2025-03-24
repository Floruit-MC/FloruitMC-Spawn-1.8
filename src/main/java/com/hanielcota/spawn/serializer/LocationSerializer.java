package com.hanielcota.spawn.serializer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LocationSerializer {

    public static Map<String, Object> serialize(Location location) {
        if (location == null || location.getWorld() == null) {
            return Collections.emptyMap();
        }

        // Cria um mapa com capacidade inicial de 6 elementos
       Map<String, Object> map = new HashMap<>(6);
        map.put("world", location.getWorld().getName());
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());
        map.put("yaw", location.getYaw());
        map.put("pitch", location.getPitch());

        return map;
    }

    public static Location deserialize(JavaPlugin plugin, Map<String, Object> map) {
        // Verifica se o mapa é nulo ou não contém as chaves essenciais
        if (map == null || !map.containsKey("world") || !map.containsKey("x") || !map.containsKey("y") || !map.containsKey("z")) {

            plugin.getLogger().warning("Mapa de localização incompleto: " + map);
            return null;
        }

        // Obtém o mundo e verifica se ele existe
        String worldName = (String) map.get("world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("Mundo não encontrado: " + worldName);
            return null;
        }

        try {
            // Converte os valores numéricos com segurança
            double x = ((Number) map.get("x")).doubleValue();
            double y = ((Number) map.get("y")).doubleValue();
            double z = ((Number) map.get("z")).doubleValue();
            float yaw = map.containsKey("yaw") ? ((Number) map.get("yaw")).floatValue() : 0.0f;
            float pitch = map.containsKey("pitch") ? ((Number) map.get("pitch")).floatValue() : 0.0f;

            return new Location(world, x, y, z, yaw, pitch);
        } catch (ClassCastException | NullPointerException e) {
            plugin.getLogger().warning("Erro ao desserializar localização: " + map + " - " + e.getMessage());
            return null;
        }
    }
}