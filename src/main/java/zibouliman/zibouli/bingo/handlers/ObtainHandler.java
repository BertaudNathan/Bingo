package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.plugin.PluginManager;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.utils.BingoObjective;
import zibouliman.zibouli.bingo.utils.WinCondition;

import java.net.http.WebSocket;

import static org.bukkit.Bukkit.getServer;
import static zibouliman.zibouli.bingo.helpers.Helpers.getDisplayNameForMaterial;
import static zibouliman.zibouli.bingo.helpers.Helpers.markObjectiveCompleted;

public class ObtainHandler implements Listener {

    public Material BingoMaterial;

    public ObtainHandler(Bingo bingo){
        Bukkit.getPluginManager().registerEvents(this,bingo);
    }

    @EventHandler
    public void ObtainItemListener(PlayerItemHeldEvent event){
        // Ignorer si le joueur a déjà gagné
        if (Bingo.PlayersCompleted.contains(event.getPlayer().getUniqueId())) {
            return;
        }

        var player = event.getPlayer();
        var newSlot = event.getNewSlot();
        if (player.getInventory().getItem(newSlot) == null){
            return;
        }

        Material heldItem = player.getInventory().getItem(newSlot).getType();
        Bukkit.getLogger().info("dans la main : " + getDisplayNameForMaterial(heldItem));

        // Vérifier tous les objectifs de type OBTAIN_ITEM
        for (int i = 0; i < Bingo.BingoObjectives.size(); i++) {
            BingoObjective objective = Bingo.BingoObjectives.get(i);

            if (objective.getType() == WinCondition.OBTAIN_ITEM) {
                Material targetMaterial = objective.getMaterial();
                Bukkit.getLogger().info("a trouver : " + getDisplayNameForMaterial(targetMaterial));

                if (heldItem == targetMaterial) {
                    // Vérifier si cet objectif n'a pas déjà été complété par ce joueur
                    if (Bingo.PlayerObjectivesCompleted.containsKey(player.getUniqueId()) &&
                        Bingo.PlayerObjectivesCompleted.get(player.getUniqueId()).contains(i)) {
                        continue; // Objectif déjà complété, passer au suivant
                    }

                    // Marquer l'objectif comme complété
                    markObjectiveCompleted(player, i);
                    return;
                }
            }
        }
    }
}
