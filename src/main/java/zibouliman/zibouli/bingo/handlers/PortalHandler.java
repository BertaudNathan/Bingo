package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import zibouliman.zibouli.bingo.Bingo;

public class PortalHandler implements Listener {

    public PortalHandler(Bingo bingo) {
        Bukkit.getPluginManager().registerEvents(this, bingo);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        World fromWorld = event.getFrom().getWorld();
        if (fromWorld == null) {
            return;
        }

        String baseWorldName = Bingo.extractBaseWorldName(fromWorld.getName());
        if (baseWorldName == null) {
            return;
        }

        World target = resolveTargetWorld(baseWorldName, fromWorld.getEnvironment(), event.getCause());
        if (target == null) {
            return;
        }

        // Force un couplage strict des dimensions pour chaque partie.
        Location spawn = target.getSpawnLocation().clone().add(0.5, 0.0, 0.5);
        event.setCanCreatePortal(true);
        event.setTo(spawn);
    }

    private World resolveTargetWorld(String baseWorldName, World.Environment fromEnv, PlayerTeleportEvent.TeleportCause cause) {
        if (cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (fromEnv == World.Environment.NORMAL) {
                return createIfMissing(baseWorldName + "_nether", World.Environment.NETHER);
            }
            if (fromEnv == World.Environment.NETHER) {
                return createIfMissing(baseWorldName, World.Environment.NORMAL);
            }
            return null;
        }

        if (cause == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (fromEnv == World.Environment.NORMAL) {
                return createIfMissing(baseWorldName + "_the_end", World.Environment.THE_END);
            }
            if (fromEnv == World.Environment.THE_END) {
                return createIfMissing(baseWorldName, World.Environment.NORMAL);
            }
        }

        return null;
    }

    private World createIfMissing(String name, World.Environment environment) {
        World existing = Bukkit.getWorld(name);
        if (existing != null) {
            return existing;
        }

        World created = Bukkit.createWorld(new WorldCreator(name).environment(environment));
        if (created != null) {
            created.setGameRule(GameRule.IMMEDIATE_RESPAWN, true);
        }
        return created;
    }
}

