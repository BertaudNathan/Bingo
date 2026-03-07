    package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import zibouliman.zibouli.bingo.Bingo;

public class PlayerRespawnHandler implements Listener {

    public PlayerRespawnHandler(Bingo bingo) {
        Bukkit.getPluginManager().registerEvents(this, bingo);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        var player = event.getPlayer();
        var world = player.getWorld();

        // Force le respawn au spawn du monde actuel
        event.setRespawnLocation(world.getSpawnLocation());

        Bukkit.getLogger().info(String.format("Joueur %s respawn dans le monde %s",
            player.getName(), world.getName()));
    }
}

