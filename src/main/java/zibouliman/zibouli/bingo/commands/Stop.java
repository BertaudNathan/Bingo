package zibouliman.zibouli.bingo.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
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
        Bingo plugin = (Bingo) Bukkit.getPluginManager().getPlugin("Bingo");
        if (plugin == null) {
            Bukkit.getLogger().severe("Plugin Bingo non trouve, impossible d'arreter proprement.");
            return true;
        }

        // Reinitialiser les donnees de partie
        Bingo.BingoObjectives.clear();
        Bingo.PlayersCompleted.clear();
        Bingo.PlayerObjectivesCompleted.clear();
        Bingo.BingoPlaying = false;
        Helpers.resetScoreboard();

        World lobbyWorld = findOrCreateLobbyWorld();
        if (lobbyWorld != null) {
            lobbyWorld.setGameRule(GameRule.IMMEDIATE_RESPAWN, true);
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setRespawnLocation(lobbyWorld.getSpawnLocation(), true);
                player.teleport(lobbyWorld.getSpawnLocation());
            });
        }

        plugin.cleanupManagedWorlds();
        Bingo.clearActiveGameWorlds();

        return true;
    }

    private World findOrCreateLobbyWorld() {
        for (World world : Bukkit.getWorlds()) {
            if (!Bingo.isManagedGameWorld(world.getName())) {
                return world;
            }
        }

        World created = Bukkit.createWorld(new WorldCreator("world"));
        if (created == null) {
            Bukkit.getLogger().severe("Aucun monde lobby disponible.");
        }
        return created;
    }
}
