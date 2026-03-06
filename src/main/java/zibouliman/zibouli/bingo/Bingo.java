package zibouliman.zibouli.bingo;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import zibouliman.zibouli.bingo.commands.Start;
import zibouliman.zibouli.bingo.handlers.ObtainHandler;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

public final class Bingo extends JavaPlugin {
    public static Material BingoMaterial;


    @Override
    public void onEnable() {
        getCommand("start").setExecutor(new Start());
        new ObtainHandler(this);
    }




    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Allez va sucer les seins de ta mère fils de pute");
    }

}
