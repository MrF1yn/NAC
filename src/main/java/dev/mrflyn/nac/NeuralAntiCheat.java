package dev.mrflyn.nac;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import dev.mrflyn.nac.clickprocessing.CpsCounterTask;
import dev.mrflyn.nac.commands.CollectDataCommand;
import dev.mrflyn.nac.commands.CraftDataSetCommand;
import dev.mrflyn.nac.commands.handler.MainCommand;
import dev.mrflyn.nac.protocol.PacketListener;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;

public class NeuralAntiCheat extends JavaPlugin {

    CpsCounterTask cpsCounterTask;
    public static File dataFolder;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        initializeDependencies();
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new PacketListener(this, PacketType.Play.Client.ARM_ANIMATION)).start();
        cpsCounterTask = new CpsCounterTask();
        cpsCounterTask.start();
        dataFolder = this.getDataFolder();
        MainCommand command = new MainCommand(
                new CollectDataCommand(this),
                new CraftDataSetCommand(this)
        );
        getCommand("nac").setExecutor(command);
        getCommand("nac").setTabCompleter(command);
    }

    @Override
    public void onDisable(){
        cpsCounterTask.stop();
    }

    private void initializeDependencies(){
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this);
        Library gson = Library.builder()
                .groupId("com{}google{}code{}gson") // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .artifactId("gson")
                .version("2.9.0")
                .id("gson")
                .relocate("com{}google",
                        "dev{}mrflyn{}nac{}libs{}com{}google")
                .build();
        Library guava = Library.builder()
                .groupId("com{}google{}guava") // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .artifactId("guava")
                .version("31.0.1-jre")
                .id("guava")
                .relocate("com{}google{}guava",
                        "dev{}mrflyn{}nac{}libs{}com{}google{}guava")
                .build();

        libraryManager.addMavenCentral();
        libraryManager.loadLibrary(gson);
        libraryManager.loadLibrary(guava);
    }

}
