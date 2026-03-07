package zibouliman.zibouli.bingo.helpers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import zibouliman.zibouli.bingo.Bingo;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class Helpers {

    /**
     * Retourne le nom localisé d'un item Material
     * Par exemple: DIAMOND_PICKAXE -> "Diamond Pickaxe"
     */
    public static String getDisplayNameForMaterial(Material material) {
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
        return Arrays.stream(name.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public static void resetScoreboard() {
         Bingo.ScoreBoard.getTeams().forEach(team -> team.unregister());
         Bingo.ScoreBoard.getObjectives().forEach(objective -> objective.unregister());
    }

    public static void Win(Player player){
        getServer().broadcastMessage(String.format("%s à gagné",player.getName()));
        getServer().dispatchCommand(getServer().getConsoleSender(),"stop-bingo");
    }

    public static String GetWinConditionString() {
        var descr = "";
        //getLogger().info(Bingo.BingoDamageCause.name());
        switch (Bingo.BingoWinCondition) {
            case OBTAIN_ITEM -> {
                return "Obtenir l'item : " + getDisplayNameForMaterial(Bingo.BingoMaterial);
            }
            case DEATH -> {
                switch (Bingo.BingoDamageCause) {
                    case FALL -> {
                        descr = "Mourir en tombant";
                    }
                    case LAVA -> {
                        descr = "Mourir dans la lave";
                    }
                    case DROWNING -> {
                        descr = "Mourir noyé";
                    }
                    case ENTITY_ATTACK -> {
                        descr = "Mourir tué par une entité (attaque directe donc pas de projectile/explosion/potions/laser)";
                    }
                    case PROJECTILE -> {
                        descr = "Mourir à cause d'un projectile";
                    }
                    case SUFFOCATION -> {
                        descr = "Mourir étouffé";
                    }
                    case FIRE,FIRE_TICK -> {
                        descr = "Mourir brûlé";
                    }
                    default -> {
                        descr = Bingo.BingoDamageCause.name();
                    }
                }
                return "Mourir de la manière suivante : " + descr;
            }
        }
        return "Condition de victoire inconnue";
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
