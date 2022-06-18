package dev.mrflyn.nac.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.mrflyn.nac.clickprocessing.CpsCounter;
import dev.mrflyn.nac.NeuralAntiCheat;
import dev.mrflyn.nac.clickprocessing.training.DataCollector;
import org.bukkit.entity.Player;

public class PacketListener extends PacketAdapter {

    NeuralAntiCheat instance;

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
        }
    }
}
