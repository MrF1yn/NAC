package dev.mrflyn.nac.checks;

import dev.mrflyn.nac.clickprocessing.training.TrainingData;

public class KillAuraChecker implements IChecker{

    public static KillAuraChecker INSTANCE = new KillAuraChecker();

    @Override
    public boolean check(TrainingData data) {
        return false;
    }

    @Override
    public int maxDataAmount() {
        return 10;
    }


}
