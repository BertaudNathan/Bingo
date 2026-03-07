package zibouliman.zibouli.bingo.commands;


import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.jspecify.annotations.NonNull;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.helpers.Helpers;
import zibouliman.zibouli.bingo.utils.PlayerResetUtils;
import zibouliman.zibouli.bingo.utils.WinCondition;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.*;
import static zibouliman.zibouli.bingo.helpers.Helpers.GetWinConditionString;
import static zibouliman.zibouli.bingo.helpers.Helpers.getDisplayNameForMaterial;

public class Start implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] strings) {
        startGame();
        return true;
    }

    public Material BingoMaterial;

    public void startGame() {
        // Réinitialiser les achievements et spawns de tous les joueurs
        PlayerResetUtils.resetForGameStart();
        Helpers.resetScoreboard();

        // Active le respawn automatique instantané sur tous les mondes existants.
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRule(GameRule.IMMEDIATE_RESPAWN, true);
        });

        // Plugin startup logic
        Bukkit.getLogger().info("plugin initialisé");
        GetWinCondition();
    }


    private void InitScoreboardForPlayer(){
        getServer().getOnlinePlayers().forEach(p->{
            Bingo.ScoreBoard.registerNewTeam(p.getDisplayName());
            var obj =  Bingo.ScoreBoard.registerNewObjective("DorinoBingo", p.getDisplayName());
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore(GetWinConditionString()).setScore(0);
            p.setScoreboard(Bingo.ScoreBoard);
        });
    }

    private Material GetRandomObtainableItem() {
        Bingo plugin = (Bingo) getPluginManager().getPlugin("Bingo");
        if (plugin == null) {
            Bukkit.getLogger().severe("Plugin Bingo non trouvé!");
            return Material.STONE;
        }
        File configfile = new File(plugin.getDataFolder(), "config.yaml");
        if (!configfile.exists()) {
            Bukkit.getLogger().severe("Fichier config.yaml introuvable à: " + configfile.getAbsolutePath());
            return Material.STONE;
        }
        var config = YamlConfiguration.loadConfiguration(configfile);
        List<String> itemList = config.getStringList("Settings.RandomItems.Blacklisted.List");
        Bukkit.getLogger().info("Nombre d'items dans la blacklist: " + itemList.size());

        List<Material> availableMaterials = Arrays.stream(Material.values())
                .filter(Material -> {return Material.isItem()|| Material.isBlock();})
                .filter(material -> !itemList.contains(material.name()))
                .collect(Collectors.toList());

        if (availableMaterials.isEmpty()) {
            availableMaterials = Collections.singletonList(Material.STONE);
        }
        return availableMaterials.get(new Random().nextInt(availableMaterials.size()));
    }

    private EntityDamageEvent.DamageCause GetRandomKillMethod() {
        Bingo plugin = (Bingo) getPluginManager().getPlugin("Bingo");
        if (plugin == null) {
            Bukkit.getLogger().severe("Plugin Bingo non trouvé!");
            return EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        }
        File configfile = new File(plugin.getDataFolder(), "config.yaml");
        if (!configfile.exists()) {
            Bukkit.getLogger().severe("Fichier config.yaml introuvable à: " + configfile.getAbsolutePath());
            return EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        }
        var config = YamlConfiguration.loadConfiguration(configfile);
        List<String> killMethodsList = config.getStringList("Settings.RandomKillMethods.Whitelisted.List");
        Bukkit.getLogger().info("Nombre de méthodes de kill: " + killMethodsList.size());
        List<EntityDamageEvent.DamageCause> availableKillMethods = Arrays.stream(EntityDamageEvent.DamageCause.values())
                .filter(damageCause -> killMethodsList.contains(damageCause.name()))
                .collect(Collectors.toList());



        if (availableKillMethods.isEmpty()) {
            return EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        }
        var index = new Random().nextInt(availableKillMethods.size());
        getLogger().info(String.valueOf(index));
        return availableKillMethods.get(index);
    }

    private void GetWinCondition(){
        var rd = new Random();
        var random = rd.nextInt(100);
        if (random < 20){
            Bingo.BingoWinCondition = WinCondition.DEATH;
            Bingo.BingoDamageCause = GetRandomKillMethod();
            getServer().broadcastMessage("Méthode de kill : " + Bingo.BingoDamageCause.name());
            var plugin = ((Bingo) getPluginManager().getPlugin("Bingo"));
            if (plugin != null) {
                Bukkit.getLogger().info("initalisation correcte : " + Bingo.BingoDamageCause.name());
                InitScoreboardForPlayer();
            }
        } else {
            Bingo.BingoMaterial = GetRandomObtainableItem();
            Bingo.BingoWinCondition = WinCondition.OBTAIN_ITEM;
            String displayName = getDisplayNameForMaterial(Bingo.BingoMaterial);
            var plugin = ((Bingo) getPluginManager().getPlugin("Bingo"));
            if (plugin != null) {
                Bukkit.getLogger().info("initalisation correcte : " + displayName);
                InitScoreboardForPlayer();
            }
        }
    }
}
