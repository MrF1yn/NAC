package dev.mrflyn.nac.utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtraUtils {
    public static String getDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return (dtf.format(now));
    }

    public static File[] listFiles(String directory){
        File file = new File(directory);
        if (!file.exists())return null;
        if(!file.isDirectory())return null;
        return file.listFiles();
    }
}
