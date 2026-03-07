package zibouliman.zibouli.bingo;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import zibouliman.zibouli.bingo.commands.Start;
import zibouliman.zibouli.bingo.commands.Stop;
import zibouliman.zibouli.bingo.handlers.DeathHandler;
import zibouliman.zibouli.bingo.handlers.ObtainHandler;
import zibouliman.zibouli.bingo.handlers.PlayerRespawnHandler;
import zibouliman.zibouli.bingo.utils.WinCondition;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import static zibouliman.zibouli.bingo.helpers.Helpers.getDisplayNameForMaterial;

public final class Bingo extends JavaPlugin {
    public static Material BingoMaterial;
    public static Scoreboard ScoreBoard;
    public static EntityDamageEvent.DamageCause BingoDamageCause;
    public static WinCondition BingoWinCondition;
    public static World BingoWorld;


    @Override
    public void onEnable() {
        getCommand("start-bingo").setExecutor(new Start());
        getCommand("stop-bingo").setExecutor(new Stop());
        new ObtainHandler(this);
        new PlayerRespawnHandler(this);
        new DeathHandler(this);

        Bingo.ScoreBoard = Bukkit.getScoreboardManager().getMainScoreboard();

        // Active le respawn automatique instantané sur tous les mondes par défaut.
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRule(GameRule.IMMEDIATE_RESPAWN, true);
        });
    }




    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Allez va sucer les seins de ta mère fils de pute");
        //Delete worlds
        Bukkit.getWorlds().forEach(world -> {
            if (world.getName().startsWith("zibouli")) {
                Bukkit.unloadWorld(world, false);
                deleteWorldFolder(world.getName());
            }
        });
    }

    private void deleteWorldFolder(String worldName) {
        File worldFolder = new File(worldName);
        if (worldFolder.exists() && worldFolder.isDirectory()) {
            deleteDirectory(worldFolder);
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

}
