package dev.mrflyn.nac.clickprocessing.training;

import dev.mrflyn.nac.utils.SpigotReflection;
import org.bukkit.entity.Player;

public class ClickData {

    private final int clicks;
    private final double tps;
    private final double pingFluctuation;

    public ClickData(int clicks, Player player) {
        this.clicks = clicks;
        this.tps = SpigotReflection.get().recentTps()[0];
        this.pingFluctuation = SpigotReflection.get().ping(player);
    }

    public int getClicks() {
        return clicks;
    }

    public double getTps() {
        return tps;
    }

    public double getPingFluctuation() {
        return pingFluctuation;
    }
}
