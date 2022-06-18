package dev.mrflyn.nac.clickprocessing;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.mrflyn.nac.NeuralAntiCheat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CpsCounter {
    public static HashMap<UUID, CpsCounter> cpsCounters= new HashMap<>();

    private NeuralAntiCheat instance;
    private Player player;
    private int rawCPS;
    private int cachedCPS;
    private boolean running;

    public CpsCounter(NeuralAntiCheat instance, Player player){
        this.instance = instance;
        this.player = player;
        if (cpsCounters.containsKey(player.getUniqueId())){
            cpsCounters.get(player.getUniqueId()).stop();
            return;
        }
        cpsCounters.put(player.getUniqueId(), this);
        running = false;
    }

    public void addClick(){
        rawCPS++;
        sendCpsInfo();
    }

    public void sendCpsInfo(){
        try {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);
            packet.getChatComponents().write(0, WrappedChatComponent.fromLegacyText("§aRaw Clicks: §6" + rawCPS + "      §aCached CPS: §6" + cachedCPS));
            packet.getBytes().write(0, (byte) 2);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void calculate(){
        if (!Bukkit.getOnlinePlayers().contains(player)) {
            stop();
            return;
        }
        cachedCPS = rawCPS;
        sendCpsInfo();
        rawCPS = 0;
    }

    public void start(){
        running = true;
    }

    public void stop() {
        running = false;
        cpsCounters.remove(player.getUniqueId());
    }

    public Player getPlayer() {
        return player;
    }

    public int getRawCPS() {
        return rawCPS;
    }

    public int getCachedCPS() {
        return cachedCPS;
    }

    public void setCachedCPS(int cachedCPS){
        this.cachedCPS = cachedCPS;
    }

    public boolean isRunning(){
        return this.running;
    }

}

