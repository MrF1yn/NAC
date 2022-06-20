package dev.mrflyn.nac.clickprocessing;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.mrflyn.nac.NeuralAntiCheat;
import dev.mrflyn.nac.checks.IChecker;
import dev.mrflyn.nac.clickprocessing.training.ClickData;
import dev.mrflyn.nac.clickprocessing.training.TrainingData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class CpsCounter {
    public static HashMap<UUID, CpsCounter> cpsCounters= new HashMap<>();

    private NeuralAntiCheat instance;
    private Player player;
    private int rawCPS;
    private int cachedCPS;
    private boolean running;
    private List<IChecker> dataCheckers;
    private int dumpedCheckers;
    private long startTime;
    private List<ClickData> clickData;

    public CpsCounter(NeuralAntiCheat instance, Player player, IChecker... checkers){
        this.instance = instance;
        this.player = player;
        if (cpsCounters.containsKey(player.getUniqueId())){
            cpsCounters.get(player.getUniqueId()).stop();
            return;
        }
        this.dataCheckers = new ArrayList<>(Arrays.asList(checkers));
        this.clickData = new ArrayList<>();
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
            packet.getChatComponents().write(0, WrappedChatComponent.fromLegacyText("§aRaw Clicks: §6" + rawCPS + "      §aCached CP1/4S: §6" + cachedCPS));
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
        if (this.dumpedCheckers>=this.dataCheckers.size())return;
        if (System.currentTimeMillis()-this.startTime<=2000L)return;
        for (int i = 0; i<this.dataCheckers.size(); i++){
            IChecker checker = this.dataCheckers.get(i);
            if (this.clickData.size()>=checker.maxDataAmount()){
                TrainingData data = new TrainingData(null, this.clickData);
                if(checker.check(data)){
                    player.sendMessage("CHEATER");
                }
                this.dataCheckers.set(i, null);
                this.dumpedCheckers++;
            }
        }

        if (this.dumpedCheckers>=this.dataCheckers.size()){
            stop();
            return;
        }
        ClickData cData = new ClickData(cachedCPS, player);
        this.clickData.add(cData);
        player.sendMessage("["+clickData.size()+"] CP1/4S: "+cData.getClicks()+" TPS: "+cData.getTps()+" PING: "+cData.getPing());
    }

    public void start(){
        running = true;
        this.startTime = System.currentTimeMillis();
    }

    public void stop() {
        running = false;
        this.clickData.clear();
        this.dataCheckers.clear();
        player.sendMessage("CpsCounter Stopped.");
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

