package zibouliman.zibouli.bingo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zibouliman.zibouli.bingo.Bingo;

import java.util.ArrayList;

public final class PlayerResetUtils {

    private PlayerResetUtils() {
    }

    public static void resetForGameStart(World w) {
        if (w == null) {
            return;
        }

        resetAllAdvancements();
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getDefaultValue());
            player.setFoodLevel(20);
            player.setSaturation(5);
            player.setExhaustion(0);
            player.setFireTicks(0);
            player.setTotalExperience(0);
            player.getInventory().clear();
            Bingo.ItemsAtBeginning.add(new ItemStack(Material.BREAD,64));
            Bingo.ItemsAtBeginning.forEach(item-> player.getInventory().addItem(item));
            player.setRespawnLocation(w.getSpawnLocation(),true);
        });
    }

    public static void resetAllAdvancements() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            resetPlayerAdvancements(player);
        }
    }

    public static void resetPlayerAdvancements(Player player) {
        for (Advancement advancement : getAllAdvancements()) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String awarded : new ArrayList<>(progress.getAwardedCriteria())) {
                progress.revokeCriteria(awarded);
            }
        }
    }

    public static void setAllPlayerSpawnToWorldSpawn(World world) {
        Location spawn = world.getSpawnLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(spawn);
            player.setRespawnLocation(spawn, true);
        }
    }

    private static Iterable<Advancement> getAllAdvancements() {
        ArrayList<Advancement> list = new ArrayList<>();
        Bukkit.advancementIterator().forEachRemaining(list::add);
        return list;
    }
}
