package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.helpers.Helpers;
import zibouliman.zibouli.bingo.utils.BingoObjective;
import zibouliman.zibouli.bingo.utils.WinCondition;

public class PlayerRespawnHandler implements Listener {

    private final Bingo plugin;

    public PlayerRespawnHandler(Bingo bingo) {
        this.plugin = bingo;
        Bukkit.getPluginManager().registerEvents(this, bingo);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        var player = event.getPlayer();
        var world = player.getWorld();
        // Réapplique un état joueur sain après le respawn côté serveur/client.
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (Bingo.BingoWorld == null){
                return;
            }
            player.teleport(Bingo.BingoWorld.getSpawnLocation());
            player.setRespawnLocation(Bingo.BingoWorld.getSpawnLocation());
            player.getInventory().setItem(0,new ItemStack(Material.BREAD,64));
        });

        Bukkit.getLogger().info(String.format("Joueur %s respawn dans le monde %s",
            player.getName(), world.getName()));
    }

    public static class AchievementHandler implements Listener {
        public AchievementHandler(Bingo bingo){
            Bukkit.getPluginManager().registerEvents(this,bingo);
        }

        // ...

        @EventHandler public void AchievementListener(PlayerAdvancementDoneEvent event) {
            if (Bingo.PlayersCompleted.contains(event.getPlayer().getUniqueId())) return;

            var player = event.getPlayer();
            NamespacedKey key = event.getAdvancement().getKey();

            // "We Need to Go Deeper" (vanilla)


            for (int i =0; i < Bingo.BingoObjectives.size(); i++) {
                BingoObjective objective = Bingo.BingoObjectives.get(i);
                if (objective.getType() == WinCondition.NETHER) {
                    if (key.equals(NamespacedKey.minecraft("story/enter_the_nether"))) {
                        if (Bingo.PlayerObjectivesCompleted.containsKey(player.getUniqueId())
                                && Bingo.PlayerObjectivesCompleted.get(player.getUniqueId()).contains(i)) {
                            continue;

                        }
                        Helpers.markObjectiveCompleted(player, i);
                    }
                }
                if (objective.getType() == WinCondition.FORTRESS) {
                    if (!key.equals(NamespacedKey.minecraft("nether/find_fortress"))) {
                        if (Bingo.PlayerObjectivesCompleted.containsKey(player.getUniqueId())
                                && Bingo.PlayerObjectivesCompleted.get(player.getUniqueId()).contains(i)) {
                            continue;

                        }
                        Helpers.markObjectiveCompleted(player, i);
                    }
                }
                if (objective.getType() == WinCondition.BASTION) {
                    if (key.equals(NamespacedKey.minecraft("nether/find_bastion"))) {
                        if (Bingo.PlayerObjectivesCompleted.containsKey(player.getUniqueId())
                                && Bingo.PlayerObjectivesCompleted.get(player.getUniqueId()).contains(i)) {
                            continue;

                        }
                        Helpers.markObjectiveCompleted(player, i);
                    }
                }if (objective.getType() == WinCondition.DIAMOND_ARMOR) {
                    if (key.equals(NamespacedKey.minecraft("story/shiny_gear"))) {
                        if (Bingo.PlayerObjectivesCompleted.containsKey(player.getUniqueId())
                                && Bingo.PlayerObjectivesCompleted.get(player.getUniqueId()).contains(i)) {
                            continue;
                        }
                        Helpers.markObjectiveCompleted(player, i);
                    }
                }
            }
        }
    }
}
