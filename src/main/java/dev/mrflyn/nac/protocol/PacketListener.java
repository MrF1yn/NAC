package dev.mrflyn.nac.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.mrflyn.nac.checks.AutoClickerChecker;
import dev.mrflyn.nac.checks.KillAuraChecker;
import dev.mrflyn.nac.clickprocessing.CpsCounter;
import dev.mrflyn.nac.NeuralAntiCheat;
import dev.mrflyn.nac.clickprocessing.training.DataCollector;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PacketListener extends PacketAdapter {

    NeuralAntiCheat instance;
    private HashMap<UUID, Long> doubleClick = new HashMap<>();
//    public static List<UUID> ignoreList = new ArrayList<>();

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
        PacketContainer packet = e.getPacket();
//        if (packet.getType() == PacketType.Play.Client.BLOCK_DIG){
//            int status = packet.getPlayerDigTypes().read(0).ordinal();
//            System.out.println(status);
//            if (status == 0){
//                if (ignoreList.contains(p.getUniqueId()))return;
//                ignoreList.add(p.getUniqueId());
//                return;
//            }
//            if (status == 1||status==2){
//                ignoreList.remove(p.getUniqueId());
//                return;
//            }
//            return;
//        }
//        if (ignoreList.contains(p.getUniqueId()))return;
        if (packet.getType()==PacketType.Play.Client.USE_ENTITY&&p.hasPermission("nac.admin"))return;// allow if admin and arm animation onle
        if (packet.getType()==PacketType.Play.Client.ARM_ANIMATION&&!p.hasPermission("nac.admin"))return; // allow if not admin and is use entity only
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
