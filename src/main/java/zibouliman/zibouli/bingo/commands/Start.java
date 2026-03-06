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
import org.jspecify.annotations.NonNull;
import zibouliman.zibouli.bingo.Bingo;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getServer;

public class Start implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] strings) {
        startGame();
        return true;
    }
    public Material BingoMaterial;
    public void startGame(){
        // Plugin startup logic
        var rd = new Random();
        Bukkit.getLogger().info("plugin initialisé");
        this.BingoMaterial = GetRandomObtainableItem();
        Bukkit.getLogger().info("item selectionné : "+ this.BingoMaterial.toString());
        var bar = Bukkit.createBossBar(this.BingoMaterial.toString(),  BarColor.GREEN, BarStyle.SOLID);
        bar.setVisible(true);
        getServer().getOnlinePlayers().forEach(bar::addPlayer);

        getServer().broadcastMessage("Bloc à obtenir : "+this.BingoMaterial.toString());
        var plugin = ((Bingo) getPluginManager().getPlugin("Bingo"));
        if ( plugin!= null){
            Bingo.BingoMaterial = this.BingoMaterial;
            Bukkit.getLogger().info("initalisation correcte : "+ this.BingoMaterial.toString());
        }
    }

    private Material GetRandomObtainableItem() {
        File configfile = new File("plugins//bingo//config.yml");
        var config = YamlConfiguration.loadConfiguration(configfile);
        List<String> itemList = config.getStringList("Settings.RandomItems.Blacklisted.List");

        List<Material> availableMaterials = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(material -> !itemList.contains(material.name()))
                .collect(Collectors.toList());

        if (availableMaterials.isEmpty()) {
            availableMaterials = Collections.singletonList(Material.STONE);
        }
        return availableMaterials.get(new Random().nextInt(availableMaterials.size()));
    }
}
