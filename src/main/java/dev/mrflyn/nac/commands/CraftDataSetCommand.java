package dev.mrflyn.nac.commands;


import com.google.gson.Gson;
import dev.mrflyn.nac.NeuralAntiCheat;
import dev.mrflyn.nac.clickprocessing.training.DataCollector;
import dev.mrflyn.nac.clickprocessing.training.TrainingData;
import dev.mrflyn.nac.clickprocessing.training.TrainingDataSet;
import dev.mrflyn.nac.commands.handler.MainCommand;
import dev.mrflyn.nac.commands.handler.SubCommand;
import dev.mrflyn.nac.utils.ExtraUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CraftDataSetCommand implements SubCommand {

    private NeuralAntiCheat instance;
    Gson gson = new Gson();

    public CraftDataSetCommand(NeuralAntiCheat instance){
        this.instance = instance;
    }

    @Override
    public boolean onSubCommand(CommandSender sender, boolean isPlayer, Command cmd, String label, String[] args) {

        List<TrainingData> trainingData = new ArrayList<>();
        File[] rawFiles = ExtraUtils.listFiles(NeuralAntiCheat.dataFolder.getPath() + "/TrainingData");
        if (rawFiles==null){
            sender.sendMessage("Couldn't find ant training data files in the directory.");
            return true;
        }
        try {
            for (File file : rawFiles) {
                TrainingData data = gson.fromJson(new FileReader(file), TrainingData.class);
                trainingData.add(data);
            }
        }catch (Exception e){
            e.printStackTrace();
            sender.sendMessage("Error occurred check console.");
            return true;
        }

        TrainingDataSet dataSet = new TrainingDataSet(trainingData);
        Bukkit.getScheduler().runTaskAsynchronously(instance, ()->{
            if (dataSet.save()==null){
                sender.sendMessage("Error occurred while trying to save Training DataSet.");
                return;
            }
            sender.sendMessage("Successfully crafted a dataset.");
        });
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "craftDataSet";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "nac.command.craftDataSet";
    }
}
