package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import zibouliman.zibouli.bingo.Bingo;

public class PlayerRespawnHandler implements Listener {

    private final Bingo plugin;

    public PlayerRespawnHandler(Bingo bingo) {
        this.plugin = bingo;
        Bukkit.getPluginManager().registerEvents(this, bingo);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        var player = event.getPlayer();
        var world = player.getWorld();



        // Réapplique un état joueur sain après le respawn côté serveur/client.
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.teleport(Bingo.BingoWorld.getSpawnLocation());
            player.setRespawnLocation(Bingo.BingoWorld.getSpawnLocation());
        });

        Bukkit.getLogger().info(String.format("Joueur %s respawn dans le monde %s",
            player.getName(), world.getName()));
    }
}
