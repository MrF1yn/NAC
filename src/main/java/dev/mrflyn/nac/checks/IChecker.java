package dev.mrflyn.nac.checks;

import dev.mrflyn.nac.clickprocessing.training.TrainingData;

public interface IChecker {

    boolean check(TrainingData data);

    int maxDataAmount();
}
