package zibouliman.zibouli.bingo.commands;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Stop implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        clearAllBossBars();

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

