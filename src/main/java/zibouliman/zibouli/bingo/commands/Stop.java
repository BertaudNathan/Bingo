package zibouliman.zibouli.bingo.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.helpers.Helpers;

public class Stop implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        Bingo.BingoMaterial = null;
        Bingo.BingoWinCondition = null;
        Bingo.BingoDamageCause = null;
        Helpers.resetScoreboard();
        var old = Bukkit.getWorlds().getFirst();

        var w = Bukkit.createWorld(new WorldCreator("zibouli"+ System.currentTimeMillis()));

        if (w != null) {
            w.setGameRule(GameRule.IMMEDIATE_RESPAWN, true);
        }
        Bingo.BingoWorld = w;
        Bukkit.getOnlinePlayers().forEach(p->{
            p.setRespawnLocation(null, true);

            if (w != null) {
                p.setRespawnLocation(w.getSpawnLocation(), true);
                p.teleport(w.getSpawnLocation());

                Bukkit.getLogger().info(String.format("Joueur %s téléporté au monde %s",
                    p.getName(), w.getName()));
            }
        });
        Bukkit.getServer().unloadWorld(old,true);
        return true;
    }
}

