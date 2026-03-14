package zibouliman.zibouli.bingo;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import zibouliman.zibouli.bingo.commands.Start;
import zibouliman.zibouli.bingo.commands.Stop;
import zibouliman.zibouli.bingo.handlers.*;
import zibouliman.zibouli.bingo.utils.BingoObjective;

import java.io.File;
import java.util.*;

import static zibouliman.zibouli.bingo.helpers.Helpers.getDisplayNameForMaterial;

public final class Bingo extends JavaPlugin {

    // Nouvelles variables pour multi-objectifs
    public static List<BingoObjective> BingoObjectives = new ArrayList<>();
    public static Scoreboard ScoreBoard;
    public static final String GAME_WORLD_PREFIX = "zibouli";

    public static World BingoWorld;
    public static World BingoNetherWorld;
    public static World BingoEndWorld;
    public static String ActiveGameWorldBaseName;

    public static Boolean BingoPlaying;
    public static Set<UUID> PlayersCompleted = new HashSet<>();
    public static List<ItemStack> ItemsAtBeginning = new ArrayList<>();
    // Map pour suivre les objectifs complétés par chaque joueur : UUID -> Set<index d'objectif>
    public static Map<UUID, Set<Integer>> PlayerObjectivesCompleted = new HashMap<>();


    @Override
    public void onEnable() {
        getCommand("start-bingo").setExecutor(new Start());
        getCommand("stop-bingo").setExecutor(new Stop());
        new ObtainHandler(this);
        new PlayerRespawnHandler(this);
        new DeathHandler(this);
        new EnchantHandler(this);
        new AnvilEvent(this);
        new PlayerRespawnHandler.AchievementHandler(this);
        new AchievementHandler(this);
        new PortalHandler(this);

        var recipe = new ShapelessRecipe(new NamespacedKey(this, "bingo_card"), new ItemStack(Material.GREEN_DYE)).addIngredient(Material.EMERALD);
        Bukkit.getServer().addRecipe(recipe);

        Bingo.ScoreBoard = Bukkit.getScoreboardManager().getMainScoreboard();
        Bingo.BingoPlaying = false;

        // Active le respawn automatique instantane sur tous les mondes par defaut.
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.IMMEDIATE_RESPAWN, true));
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Arret du plugin Bingo");
        cleanupManagedWorlds();
    }

    public static boolean isManagedGameWorld(String worldName) {
        return worldName != null && worldName.startsWith(GAME_WORLD_PREFIX);
    }

    public static String extractBaseWorldName(String worldName) {
        if (!isManagedGameWorld(worldName)) {
            return null;
        }
        if (worldName.endsWith("_nether")) {
            return worldName.substring(0, worldName.length() - "_nether".length());
        }
        if (worldName.endsWith("_the_end")) {
            return worldName.substring(0, worldName.length() - "_the_end".length());
        }
        return worldName;
    }

    public void cleanupManagedWorlds() {
        // Copie defensive: on unload/supprime pendant l'iteration.
        List<World> worlds = new ArrayList<>(Bukkit.getWorlds());
        for (World world : worlds) {
            if (!isManagedGameWorld(world.getName())) {
                continue;
            }
            Bukkit.unloadWorld(world, false);
            deleteWorldFolder(world.getName());
        }
    }

    private void deleteWorldFolder(String worldName) {
        File worldFolder = new File(getServer().getWorldContainer(), worldName);
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

    public static void setActiveGameWorlds(String baseName, World overworld, World nether, World end) {
        ActiveGameWorldBaseName = baseName;
        BingoWorld = overworld;
        BingoNetherWorld = nether;
        BingoEndWorld = end;
    }

    public static void clearActiveGameWorlds() {
        ActiveGameWorldBaseName = null;
        BingoWorld = null;
        BingoNetherWorld = null;
        BingoEndWorld = null;
    }

}
