package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.utils.BingoObjective;
import zibouliman.zibouli.bingo.utils.WinCondition;

import java.util.List;

import static zibouliman.zibouli.bingo.helpers.Helpers.getDisplayNameForMaterial;
import static zibouliman.zibouli.bingo.helpers.Helpers.markObjectiveCompleted;

public class AchievementHandler implements Listener {
        public AchievementHandler(Bingo bingo) {
            Bukkit.getPluginManager().registerEvents(this,bingo);
        }

        @EventHandler
        public void onPlayerAdvancementDoneEvent( org.bukkit.event.player.PlayerAdvancementDoneEvent event) {


            if (Bingo.PlayersCompleted.contains(event.getPlayer().getUniqueId())) {
                return;
            }

           var player = event.getPlayer();
            var advancementKey = event.getAdvancement().getKey().toString();
            for (int i = 0; i < Bingo.BingoObjectives.size(); i++) {
                BingoObjective objective = Bingo.BingoObjectives.get(i);

                if (objective.getType() == WinCondition.ADVANCEMENT) {
                    String targetAdv = objective.getAdvancement().get(0);
                    Bukkit.getLogger().info("a Faire : " + (targetAdv));
                    Bukkit.getLogger().info("FAIT : " + advancementKey);

                    if (advancementKey.equals(targetAdv)) {
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
