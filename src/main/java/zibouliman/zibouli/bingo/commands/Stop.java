package zibouliman.zibouli.bingo.commands;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldCreator;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.helpers.Helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Stop implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        Bingo.BingoMaterial = null;
        Helpers.resetScoreboard();
        var old = Bukkit.getWorlds().getFirst();
        var w = Bukkit.createWorld(new WorldCreator("zibouli"+ System.currentTimeMillis()));
        Bukkit.getOnlinePlayers().forEach(p->{
            // Réinitialiser le bed spawn pour éviter le message "lit obstrué ou cassé"
            p.setBedSpawnLocation(null, true);
            // Définir le respawn au spawn du nouveau monde (force = true)
            p.setRespawnLocation(w.getSpawnLocation(), true);
            // Téléporter le joueur
            p.teleport(w.getSpawnLocation());

            Bukkit.getLogger().info(String.format("Joueur %s téléporté au monde %s",
                p.getName(), w.getName()));
        });
        Bukkit.getServer().unloadWorld(old,true);
        return true;
    }

    private void clearAllBossBars() {
        Iterator<KeyedBossBar> iterator = Bukkit.getBossBars();
        List<NamespacedKey> keysToRemove = new ArrayList<>();

        while (iterator.hasNext()) {
            KeyedBossBar bossBar = iterator.next();
            bossBar.removeAll();
            keysToRemove.add(bossBar.getKey());
        }

        for (NamespacedKey key : keysToRemove) {
            Bukkit.removeBossBar(key);
        }
    }
}

