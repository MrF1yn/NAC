package dev.mrflyn.nac.commands;


import dev.mrflyn.nac.NeuralAntiCheat;
import dev.mrflyn.nac.clickprocessing.training.DataCollector;
import dev.mrflyn.nac.commands.handler.MainCommand;
import dev.mrflyn.nac.commands.handler.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CollectDataCommand implements SubCommand {

    private NeuralAntiCheat instance;

    public CollectDataCommand(NeuralAntiCheat instance){
        this.instance = instance;
    }

    @Override
    public boolean onSubCommand(CommandSender sender, boolean isPlayer, Command cmd, String label, String[] args) {
        if(!isPlayer)return true;
        if (args.length<1){
            sender.sendMessage("Please specify the label.");
            return true;
        }
        new DataCollector((Player) sender, args[0], instance);
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            results.add("CHEATER");
            results.add("LEGIT");
            return MainCommand.sortedResults(args[0], results);
        }
        return null;
    }

    @Override
    public String getName() {
        return "collectData";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "nac.command.collectData";
    }
}
