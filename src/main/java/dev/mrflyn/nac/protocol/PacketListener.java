package dev.mrflyn.nac.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.mrflyn.nac.checks.AutoClickerChecker;
import dev.mrflyn.nac.checks.KillAuraChecker;
import dev.mrflyn.nac.clickprocessing.CpsCounter;
import dev.mrflyn.nac.NeuralAntiCheat;
import dev.mrflyn.nac.clickprocessing.training.DataCollector;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PacketListener extends PacketAdapter {

    NeuralAntiCheat instance;
    private HashMap<UUID, Long> doubleClick = new HashMap<>();

    public PacketListener(NeuralAntiCheat plugin, PacketType... types) {
        super(plugin, ListenerPriority.MONITOR, types);
        instance = plugin;
    }

    public PacketListener(NeuralAntiCheat plugin) {
        super(plugin, PacketType.Play.Client.getInstance().values());
        instance = plugin;
    }
    @Override
    public void onPacketReceiving(PacketEvent e){
        Player p = e.getPlayer();
        DataCollector dataCollector = DataCollector.dataCollectors.get(p.getUniqueId());
        if (dataCollector!=null && !dataCollector.isRunning()){
            dataCollector.start();
            return;
        }
        if (CpsCounter.cpsCounters.containsKey(p.getUniqueId())){
            CpsCounter.cpsCounters.get(p.getUniqueId()).addClick();
            return;
        }
        if (p.hasPermission("nac.bypass"))return;
        if (doubleClick.containsKey(p.getUniqueId())){
            long oldTime = doubleClick.get(p.getUniqueId());
            if (System.currentTimeMillis()-oldTime<=2000L){
                CpsCounter cpsCounter = new CpsCounter(instance, p, AutoClickerChecker.INSTANCE, KillAuraChecker.INSTANCE);
                cpsCounter.start();
            }
            doubleClick.remove(p.getUniqueId());
        }else {
            doubleClick.put(p.getUniqueId(), System.currentTimeMillis());
        }
    }
}
