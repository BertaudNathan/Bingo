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
import zibouliman.zibouli.bingo.utils.BingoObjective;
import zibouliman.zibouli.bingo.utils.PlayerResetUtils;
import zibouliman.zibouli.bingo.utils.WinCondition;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.*;
import static zibouliman.zibouli.bingo.helpers.Helpers.getDisplayNameForMaterial;

public class Start implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] strings) {
        int numberOfObjectives = 1; // Par défaut : 1 objectif

        if (strings.length > 0) {
            try {
                numberOfObjectives = Integer.parseInt(strings[0]);
                if (numberOfObjectives < 1) {
                    commandSender.sendMessage("§cLe nombre d'objectifs doit être au moins 1.");
                    return false;
                }
                if (numberOfObjectives > 10) {
                    commandSender.sendMessage("§cLe nombre d'objectifs ne peut pas dépasser 10.");
                    return false;
                }
            } catch (NumberFormatException e) {
                commandSender.sendMessage("§cArgument invalide. Utilisez: /start-bingo <nombre>");
                return false;
            }
        }

        startGame(numberOfObjectives);
        return true;
    }

    public Material BingoMaterial;

    public void startGame(int numberOfObjectives) {
        // Réinitialiser les achievements et spawns de tous les joueurs
        PlayerResetUtils.resetForGameStart();
        Helpers.resetScoreboard();

        // Réinitialiser la liste des joueurs qui ont terminé
        Bingo.PlayersCompleted.clear();
        // Réinitialiser les objectifs complétés par chaque joueur
        Bingo.PlayerObjectivesCompleted.clear();

        // Active le respawn automatique instantané sur tous les mondes existants.
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRule(GameRule.IMMEDIATE_RESPAWN, true);
        });

        // Plugin startup logic
        Bukkit.getLogger().info("plugin initialisé avec " + numberOfObjectives + " objectif(s)");
        GenerateObjectives(numberOfObjectives);
        InitScoreboardForPlayer();

        // Broadcast des objectifs
        getServer().broadcastMessage("§6========== OBJECTIFS DU BINGO ==========");
        for (int i = 0; i < Bingo.BingoObjectives.size(); i++) {
            getServer().broadcastMessage("§e" + (i + 1) + ". §f" + GetObjectiveString(Bingo.BingoObjectives.get(i)));
        }
        getServer().broadcastMessage("§6=========================================");
    }


    private void InitScoreboardForPlayer(){
        getServer().getOnlinePlayers().forEach(p->{
            Bingo.ScoreBoard.registerNewTeam(p.getDisplayName());
            var obj =  Bingo.ScoreBoard.registerNewObjective("DorinoBingo", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName("§6§lBINGO");

            // Afficher tous les objectifs dans le scoreboard
            int score = 0;
            for (BingoObjective objective : Bingo.BingoObjectives) {
                String objectiveText = GetObjectiveString(objective);
                // Limiter la longueur pour éviter les problèmes d'affichage
                if (objectiveText.length() > 40) {
                    objectiveText = objectiveText.substring(0, 37) + "...";
                }
                obj.getScore("§7" + objectiveText).setScore(score++);
            }

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

    private void GenerateObjectives(int numberOfObjectives) {
        Bingo.BingoObjectives.clear();

        for (int i = 0; i < numberOfObjectives; i++) {
            var rd = new Random();
            var random = rd.nextInt(100);

            if (random < 20) { // 20% chance de mort
                EntityDamageEvent.DamageCause damageCause = GetRandomKillMethod();
                Bingo.BingoObjectives.add(new BingoObjective(WinCondition.DEATH, damageCause));
                Bukkit.getLogger().info("Objectif " + (i + 1) + " : Mort par " + damageCause.name());
            } else { // 80% chance d'item
                Material material = GetRandomObtainableItem();
                Bingo.BingoObjectives.add(new BingoObjective(WinCondition.OBTAIN_ITEM, material));
                String displayName = getDisplayNameForMaterial(material);
                Bukkit.getLogger().info("Objectif " + (i + 1) + " : Obtenir " + displayName);
            }
        }

        // Mettre à jour les variables pour compatibilité avec l'ancien code
        if (!Bingo.BingoObjectives.isEmpty()) {
            BingoObjective firstObjective = Bingo.BingoObjectives.get(0);
            Bingo.BingoWinCondition = firstObjective.getType();
            if (firstObjective.getType() == WinCondition.OBTAIN_ITEM) {
                Bingo.BingoMaterial = firstObjective.getMaterial();
            } else if (firstObjective.getType() == WinCondition.DEATH) {
                Bingo.BingoDamageCause = firstObjective.getDamageCause();
            }
        }
    }

    private String GetObjectiveString(BingoObjective objective) {
        switch (objective.getType()) {
            case OBTAIN_ITEM:
                return "Obtenir : " + getDisplayNameForMaterial(objective.getMaterial());
            case DEATH:
                return GetDeathDescription(objective.getDamageCause());
            default:
                return "Objectif inconnu";
        }
    }

    private String GetDeathDescription(EntityDamageEvent.DamageCause cause) {
        String descr;
        switch (cause) {
            case FALL:
                descr = "Mourir en tombant";
                break;
            case LAVA:
                descr = "Mourir dans la lave";
                break;
            case DROWNING:
                descr = "Mourir noyé";
                break;
            case ENTITY_ATTACK:
                descr = "Mourir tué par une entité";
                break;
            case PROJECTILE:
                descr = "Mourir par projectile";
                break;
            case SUFFOCATION:
                descr = "Mourir étouffé";
                break;
            case FIRE:
            case FIRE_TICK:
                descr = "Mourir brûlé";
                break;
            default:
                descr = "Mourir de : " + cause.name();
                break;
        }
        return descr;
    }
}
