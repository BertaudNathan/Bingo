package zibouliman.zibouli.bingo.commands;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.jspecify.annotations.NonNull;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.helpers.Helpers;
import zibouliman.zibouli.bingo.utils.PlayerResetUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getServer;
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
        // Plugin startup logic
        var rd = new Random();
        Bukkit.getLogger().info("plugin initialisé");
        this.BingoMaterial = GetRandomObtainableItem();
        String displayName = getDisplayNameForMaterial(this.BingoMaterial);
        Bukkit.getLogger().info("item selectionné : " + displayName);
        getServer().broadcastMessage("Bloc à obtenir : " + displayName);
        var plugin = ((Bingo) getPluginManager().getPlugin("Bingo"));
        if (plugin != null) {
            Bingo.BingoMaterial = this.BingoMaterial;
            Bukkit.getLogger().info("initalisation correcte : " + displayName);

            getServer().getOnlinePlayers().forEach(p->{
                Bingo.ScoreBoard.registerNewTeam(p.getDisplayName());
                var obj =  Bingo.ScoreBoard.registerNewObjective("Bloc a trouver", p.getDisplayName());
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                obj.setDisplayName(getDisplayNameForMaterial(Bingo.BingoMaterial));
                p.setScoreboard(Bingo.ScoreBoard);
            });
        }
    }

    private Material GetRandomObtainableItem() {
        File configfile = new File("plugins//bingo//config.yml");
        var config = YamlConfiguration.loadConfiguration(configfile);
        List<String> itemList = config.getStringList("Settings.RandomItems.Blacklisted.List");

        List<Material> availableMaterials = Arrays.stream(Material.values())
                .filter(Material -> {return Material.isItem()|| Material.isBlock();})
                .filter(material -> !itemList.contains(material.name()))
                .collect(Collectors.toList());

        if (availableMaterials.isEmpty()) {
            availableMaterials = Collections.singletonList(Material.STONE);
        }
        availableMaterials.forEach(
                material -> Bukkit.getLogger().info(material.name())
        );
        return availableMaterials.get(new Random().nextInt(availableMaterials.size()));
    }
}

