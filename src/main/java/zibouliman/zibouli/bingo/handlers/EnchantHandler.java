package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.helpers.Helpers;

public class EnchantHandler  implements Listener {

    private final Bingo plugin;

    public EnchantHandler(Bingo bingo) {
        this.plugin = bingo;
        Bukkit.getPluginManager().registerEvents(this, bingo);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        var player = event.getEnchanter();

        // Ignorer si le joueur a déjà gagné
        if (Bingo.PlayersCompleted.contains(player.getUniqueId())) {
            return;
        }

        // Vérifier tous les objectifs de type ENCHANT
        for (int i = 0; i < Bingo.BingoObjectives.size(); i++) {
            var objective = Bingo.BingoObjectives.get(i);

            if (objective.getType() == zibouliman.zibouli.bingo.utils.WinCondition.ENCHANT) {
                    // Vérifier si cet objectif n'a pas déjà été complété par ce joueur
                    if (Bingo.PlayerObjectivesCompleted.containsKey(player.getUniqueId()) &&
                        Bingo.PlayerObjectivesCompleted.get(player.getUniqueId()).contains(i)) {
                        continue; // Objectif déjà complété, passer au suivant
                    }
                    // Marquer l'objectif comme complété
                    Helpers.markObjectiveCompleted(player, i);
                    return;

            }
        }
    }

    }

