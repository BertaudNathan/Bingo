package zibouliman.zibouli.bingo.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.plugin.PluginManager;
import zibouliman.zibouli.bingo.Bingo;

import java.net.http.WebSocket;

import static org.bukkit.Bukkit.getServer;

public class ObtainHandler implements Listener {

    public Material BingoMaterial;

    public ObtainHandler(Bingo bingo){
        Bukkit.getPluginManager().registerEvents(this,bingo);
    }

    @EventHandler
    public void ObtainItemListener(PlayerItemHeldEvent event){
        var player = event.getPlayer();
        var newSlot = event.getNewSlot();
        if (player.getInventory().getItem(newSlot) == null){
            return;
        }
        Bukkit.getLogger().info(player.getInventory().getItem(newSlot).getData().toString());
        if (Bingo.BingoMaterial != null && Bingo.BingoMaterial.equals(player.getInventory().getItem(newSlot).getData().toString())){
            getServer().broadcastMessage(String.format("%s à gagné",player.getName()));
        }
    }
}
