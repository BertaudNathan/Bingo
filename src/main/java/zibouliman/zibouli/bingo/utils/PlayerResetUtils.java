package zibouliman.zibouli.bingo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public final class PlayerResetUtils {

    private PlayerResetUtils() {
    }

    public static void resetForGameStart() {
        resetAllAdvancements();

        if (Bukkit.getWorlds().isEmpty()) {
            return;
        }

        World world = Bukkit.getWorlds().get(0);
        setAllPlayerSpawnToWorldSpawn(world);
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

