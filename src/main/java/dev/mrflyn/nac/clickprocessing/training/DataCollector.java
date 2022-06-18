package dev.mrflyn.nac.clickprocessing.training;

import dev.mrflyn.nac.NeuralAntiCheat;
import dev.mrflyn.nac.clickprocessing.CpsCounter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class DataCollector {

    public static HashMap<UUID, DataCollector>dataCollectors = new HashMap<>();

    private Player player;
    private boolean isRunning;
    private TimerTask task;
    private Timer timer;
    private NeuralAntiCheat instance;
    private List<ClickData> data;
    private CpsCounter cpsCounter;
    private String label;

    public DataCollector(Player player, String label, NeuralAntiCheat instance){
        isRunning = false;
        this.player = player;
        this.label = label;
        data = new ArrayList<>();
        this.cpsCounter = CpsCounter.cpsCounters.get(player.getUniqueId());
        if (cpsCounter==null) {
            cpsCounter = new CpsCounter(instance, player);
            cpsCounter.start();
        }else if(!cpsCounter.isRunning())cpsCounter.start();

        this.instance = instance;
        timer = new Timer("DataCollector:"+ player.getName());
        task = new TimerTask() {
            int i = 1;
            @Override
            public void run() {
                if ((!Bukkit.getOnlinePlayers().contains(player))||(!CpsCounter.cpsCounters.containsKey(player.getUniqueId()))||(!cpsCounter.isRunning())){
                    stop(false);
                    return;
                }
                if (i>5){
                    stop(true);
                    return;
                }
                ClickData clickData = new ClickData(cpsCounter.getCachedCPS(), player);
                data.add(clickData);
                player.sendMessage("["+i+"] CPS: "+clickData.getClicks()+" TPS: "+clickData.getTps()+" PING: "+clickData.getPing());
                i++;
            }
        };
        if (dataCollectors.containsKey(player.getUniqueId())){
            dataCollectors.get(player.getUniqueId()).stop(false);
            return;
        }
        dataCollectors.put(player.getUniqueId(), this);
        player.sendMessage("Training DataCollector is Ready!");
    }

    public void start(){
        if (isRunning)return;
        timer.scheduleAtFixedRate(task, 2000L, 1000L);
        isRunning = true;
        player.sendMessage("Training Started!");
    }


    public void stop(boolean save){
        if (!isRunning)return;
        task.cancel();
        timer.cancel();
        dataCollectors.remove(player.getUniqueId());
        if (save){
            TrainingData trainingData = new TrainingData(label, this.data);
            if (trainingData.save()==null){
                player.sendMessage("Error occurred while trying to save training data!");
                return;
            }
            player.sendMessage("Training Data saved!");
            return;
        }
        player.sendMessage("Aborted Old DataCollector");
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public List<ClickData> getData() {
        return data;
    }

    public CpsCounter getCpsCounter() {
        return cpsCounter;
    }

    public String getLabel() {
        return label;
    }
}
