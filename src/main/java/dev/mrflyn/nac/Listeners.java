package dev.mrflyn.nac;

import com.comphenix.protocol.ProtocolLibrary;
import dev.mrflyn.nac.clickprocessing.CpsCounter;
import dev.mrflyn.nac.protocol.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {
    NeuralAntiCheat instance;

    public Listeners(NeuralAntiCheat instance){
        this.instance = instance;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if (e.getMessage().equals("cps")){
                CpsCounter cpsCounter = new CpsCounter(instance, e.getPlayer());
                cpsCounter.start();
        }
        else if (e.getMessage().equals("stop")) {
            Bukkit.getScheduler().runTask(instance, () -> {
                if (CpsCounter.cpsCounters.containsKey(e.getPlayer().getUniqueId())){
                    CpsCounter.cpsCounters.get(e.getPlayer().getUniqueId()).stop();
                }
            });
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
//        PacketListener.ignoreList.remove(e.getPlayer().getUniqueId());
    }

}
