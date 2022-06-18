package dev.mrflyn.nac;

import dev.mrflyn.nac.clickprocessing.CpsCounter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

}
