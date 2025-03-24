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

@CommandAlias("setspawn")
@Description("Define o ponto de spawn principal ou de fallback")
@AllArgsConstructor
public class SetSpawnCommand extends BaseCommand {

    private LocationUtils locationUtils;

    @Default
    @CommandPermission("spawn.set")
    public void onCommand(Player player, String[] args) {
        Location location = player.getLocation();

        if (args.length == 0) {
            player.sendMessage("§cVocê deve usar /setspawn <spawn|fallback> para definir o spawn.");
            return;
        }

        String type = args[0].toLowerCase();

        if (!type.equals("spawn") && !type.equals("fallback")) {
            player.sendMessage("§cTipo inválido! Use 'spawn' para spawn principal ou 'fallback' para reserva.");
            return;
        }

        if (type.equals("spawn")) {
            locationUtils.saveLocationByName("spawn", location);
            player.sendMessage("§aSpawn principal definido com sucesso na sua posição atual!");
            return;
        }

        locationUtils.saveLocationByName("fallback", location);
        player.sendMessage("§aSpawn de fallback definido com sucesso na sua posição atual!");
    }
}