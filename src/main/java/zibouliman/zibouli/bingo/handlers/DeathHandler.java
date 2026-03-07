package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.utils.WinCondition;

import static zibouliman.zibouli.bingo.helpers.Helpers.Win;

public class DeathHandler implements Listener {

    private final Bingo plugin;

    public DeathHandler(Bingo bingo) {
        this.plugin = bingo;
        Bukkit.getPluginManager().registerEvents(this, bingo);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();

        if (Bingo.BingoWinCondition != WinCondition.DEATH) {
            return;
        }

        var damageEvent = player.getLastDamageCause();
        if (damageEvent != null && damageEvent.getCause() == Bingo.BingoDamageCause) {
            Win(player);
        }
    }
}
