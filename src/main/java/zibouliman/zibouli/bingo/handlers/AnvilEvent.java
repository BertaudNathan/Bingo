package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.utils.BingoObjective;
import zibouliman.zibouli.bingo.utils.WinCondition;

import java.util.List;

import static zibouliman.zibouli.bingo.helpers.Helpers.markObjectiveCompleted;

public class AnvilEvent implements Listener {

    public AnvilEvent(Bingo bingo) {
        Bukkit.getPluginManager().registerEvents(this, bingo);
    }

    @EventHandler
    public void onAnvilItemRenamed(PrepareAnvilEvent event) {
        if (event.getViewers().isEmpty()) {
            return;
        }


        Player player = (Player) event.getViewers().getFirst();

        // Ignorer si le joueur a déjà gagné
        if (Bingo.PlayersCompleted.contains(player.getUniqueId())) {
            return;
        }
        // Vérifier tous les objectifs de type RENAME
        for (int i = 0; i < Bingo.BingoObjectives.size(); i++) {
            BingoObjective objective = Bingo.BingoObjectives.get(i);

            if (objective.getType() == WinCondition.RENAME) {
                AnvilInventory inventory = event.getInventory();
                ItemStack result = event.getResult();
                ItemStack firstItem = inventory.getItem(0);

                // Vérifier si un objet a été renommé
                if (result == null || firstItem == null) {
                    return;
                }

                // Vérifier si l'item a un nom personnalisé (renommé)
                if (result.hasItemMeta()) {
                    var meta = result.getItemMeta();
                    if (meta != null && meta.hasDisplayName()) {
                        String customName = meta.getDisplayName();

                        // Vérifier si c'est bien un renommage (pas juste une réparation)
                        boolean isRenamed = !customName.isEmpty();

                        if (isRenamed) {
                            Bukkit.getLogger().info(player.getName() + " a renommé un objet: " + result.getType() + " -> " + customName);
                            markObjectiveCompleted(player, i);

                        }
                    }
                }
            }
        }


    }
}
