package dev.mrflyn.nac.checks;

import dev.mrflyn.nac.clickprocessing.training.TrainingData;

public class AutoClickerChecker implements IChecker{
    public static AutoClickerChecker INSTANCE = new AutoClickerChecker();
    //TODO: MODEL

    public AutoClickerChecker(){

    }

    @Override
    public boolean check(TrainingData data){
        System.out.println(data.getClickDataList().toString());
        return true;
    }

    @Override
    public int maxDataAmount() {
        return 10;
    }


}
