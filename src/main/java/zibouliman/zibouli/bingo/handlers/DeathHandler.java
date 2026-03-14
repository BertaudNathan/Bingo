package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.utils.BingoObjective;
import zibouliman.zibouli.bingo.utils.WinCondition;

import static zibouliman.zibouli.bingo.helpers.Helpers.markObjectiveCompleted;

public class DeathHandler implements Listener {

    private final Bingo plugin;

    public DeathHandler(Bingo bingo) {
        this.plugin = bingo;
        Bukkit.getPluginManager().registerEvents(this, bingo);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();

        // Ignorer si le joueur a déjà gagné
        if (Bingo.PlayersCompleted.contains(player.getUniqueId())) {
            return;
        }

        var damageEvent = player.getLastDamageCause();
        if (damageEvent == null) {
            return;
        }

        // Vérifier tous les objectifs de type DEATH
        for (int i = 0; i < Bingo.BingoObjectives.size(); i++) {
            BingoObjective objective = Bingo.BingoObjectives.get(i);

            if (objective.getType() == WinCondition.DEATH) {
                if (damageEvent.getCause() == objective.getDamageCause()) {
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
            if (objective.getType() == WinCondition.BIOME) {
                if (player.getLocation().getBlock().getBiome() == objective.getBiome()) {
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
