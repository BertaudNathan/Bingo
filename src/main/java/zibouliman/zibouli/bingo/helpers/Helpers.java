package zibouliman.zibouli.bingo.helpers;

import org.bukkit.Material;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import zibouliman.zibouli.bingo.Bingo;

import java.util.Arrays;
import java.util.stream.Collectors;

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




}
