package zibouliman.zibouli.bingo.helpers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import zibouliman.zibouli.bingo.Bingo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class Helpers {

    /**
     * Retourne le nom localisé d'un item Material
     * Par exemple: DIAMOND_PICKAXE -> "Diamond Pickaxe"
     */
    public static String getDisplayNameForMaterial(List<Material> material) {
        var str = "";
        if (material == null) {
            return null;
        }
        for (Material m : material) {
            var itemStack = new org.bukkit.inventory.ItemStack(m);
            var meta = itemStack.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                str += meta.getDisplayName();
            }

            // Fallback: convertir le nom technique en nom lisible
            String name = m.toString()
                    .replace("_", " ")
                    .toLowerCase();

            // Capitaliser la première lettre de chaque mot
            str += Arrays.stream(name.split(" "))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                    .collect(Collectors.joining(" "));
            getLogger().info(str);
            if (material.get(material.size() - 1) != m){
                str += "/";
            }
        }
        return str;

    }

    public static String getDisplayNameForMaterial(Material material) {
        var str = "";
        if (material == null) {
            return null;
        }

            var itemStack = new org.bukkit.inventory.ItemStack(material);
            var meta = itemStack.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                return meta.getDisplayName();
            }

            // Fallback: convertir le nom technique en nom lisible
            String name = material.toString()
                    .replace("_", " ")
                    .toLowerCase();

            // Capitaliser la première lettre de chaque mot
            return  Arrays.stream(name.split(" "))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                    .collect(Collectors.joining(" "));
        }



    public static void resetScoreboard() {
         Bingo.ScoreBoard.getTeams().forEach(team -> team.unregister());
         Bingo.ScoreBoard.getObjectives().forEach(objective -> objective.unregister());
    }

    /**
     * Marque un objectif comme complété pour un joueur et vérifie si tous les objectifs sont terminés
     * @param player Le joueur qui a complété l'objectif
     * @param objectiveIndex L'index de l'objectif complété dans la liste BingoObjectives
     * @return true si le joueur a complété tous les objectifs, false sinon
     */
    public static boolean markObjectiveCompleted(Player player, int objectiveIndex) {
        // Initialiser le set d'objectifs pour ce joueur s'il n'existe pas
        Bingo.PlayerObjectivesCompleted.putIfAbsent(player.getUniqueId(), new HashSet<>());

        Set<Integer> completedObjectives = Bingo.PlayerObjectivesCompleted.get(player.getUniqueId());

        // Ajouter l'objectif complété
        completedObjectives.add(objectiveIndex);

        // Afficher la progression
        int totalObjectives = Bingo.BingoObjectives.size();
        int completedCount = completedObjectives.size();

        player.sendMessage("§a✓ Objectif " + (objectiveIndex + 1) + " complété ! §7(" + completedCount + "/" + totalObjectives + ")");
        getServer().broadcastMessage("§l✓ Objectif " + (objectiveIndex + 1) + " complété par " + player.getName());
        // Vérifier si tous les objectifs sont complétés
        if (completedCount >= totalObjectives) {
            Win(player);
            return true;
        }

        return false;
    }

    public static void Win(Player player){
        // Ajouter le joueur à la liste des joueurs qui ont terminé
        if (!Bingo.PlayersCompleted.add(player.getUniqueId())) {
            // Le joueur a déjà gagné, ne rien faire
            return;
        }

        getServer().broadcastMessage("§6§l" + player.getName() + " §ea terminé tous les objectifs du BINGO !");
        getServer().dispatchCommand(getServer().getConsoleSender(),"stop-bingo");
    }

     private static void deleteWorldFolder(String worldName) {
         var worldFolder = new java.io.File(getServer().getWorldContainer(), worldName);
         if (worldFolder.exists()) {
             deleteDirectory(worldFolder);
         }
     }

     private static void deleteDirectory(java.io.File directory) {
         if (directory.isDirectory()) {
             for (java.io.File file : directory.listFiles()) {
                 deleteDirectory(file);
             }
         }
         directory.delete();
     }




}
