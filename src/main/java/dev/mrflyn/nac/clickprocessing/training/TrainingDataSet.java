package dev.mrflyn.nac.clickprocessing.training;

import com.google.gson.Gson;
import dev.mrflyn.nac.NeuralAntiCheat;
import dev.mrflyn.nac.utils.ExtraUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class TrainingDataSet {

    private String date;
    private List<TrainingData> data;

    public TrainingDataSet(List<TrainingData> data) {
        this.data = data;
        this.date = ExtraUtils.getDate();
    }

    public String getDate() {
        return date;
    }

    public List<TrainingData> getData() {
        return data;
    }

    public File save(){

        try {
            File file = new File(NeuralAntiCheat.dataFolder.getPath() + "/TrainingDataSets", "dataset_" + date + ".json");

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(this, writer);
            writer.flush();
            writer.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
