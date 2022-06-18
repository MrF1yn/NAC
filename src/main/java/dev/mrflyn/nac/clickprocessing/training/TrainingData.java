package dev.mrflyn.nac.clickprocessing.training;

import com.google.gson.Gson;
import dev.mrflyn.nac.NeuralAntiCheat;
import dev.mrflyn.nac.utils.ExtraUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class TrainingData {

    private String label;
    private String date;
    private List<ClickData> clickDataList;

    public TrainingData(String label, List<ClickData> clickDataList) {
        this.label = label;
        this.clickDataList = clickDataList;
        this.date = ExtraUtils.getDate();
    }

    public String getDate(){
        return date;
    }

    public String getLabel() {
        return label;
    }

    public List<ClickData> getClickDataList() {
        return clickDataList;
    }

    public File save(){

        try {
            File file = new File(NeuralAntiCheat.dataFolder.getPath() + "/TrainingData", label + "_" + date + ".json");

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            System.out.println(new Gson().toJson(this).toString());
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
