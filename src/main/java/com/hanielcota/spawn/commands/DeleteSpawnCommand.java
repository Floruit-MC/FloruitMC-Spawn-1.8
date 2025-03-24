package com.hanielcota.spawn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.hanielcota.spawn.utils.LocationUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@CommandAlias("deletespawn|spawnremove|delspawn")
@Description("Remove o ponto de spawn principal ou de fallback")
@AllArgsConstructor
public class DeleteSpawnCommand extends BaseCommand {

    private LocationUtils locationUtils;

    @Default
    @CommandPermission("spawn.delete")
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§cVocê deve usar /deletespawn <spawn|fallback> para remover um spawn.");
            return;
        }

        String type = args[0].toLowerCase();

        if (!type.equals("spawn") && !type.equals("fallback")) {
            player.sendMessage("§cTipo inválido! Use 'spawn' para spawn principal ou 'fallback' para reserva.");
            return;
        }

        if (!locationUtils.hasLocationByName(type)) {
            player.sendMessage("§cO " + type + " não está definido!");
            return;
        }

        locationUtils.removeLocationByName(type);
        player.sendMessage("§a" + (type.equals("spawn") ? "Spawn principal" : "Spawn de fallback") + " removido com sucesso!");
    }
}