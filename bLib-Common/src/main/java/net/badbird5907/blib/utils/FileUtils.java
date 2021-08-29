package net.badbird5907.blib.utils;

import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileUtils {
    public static String readFileToString(File file){
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(file.getPath()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n")); //not the best solution but it works
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
    public static void printObjectToFile(File file, Object o){
        printObjectToFile(file, o,new Gson());
    }
    @SneakyThrows
    public static void printObjectToFile(File file, Object o,Gson gson){
        if (!file.exists()){
            file.createNewFile();
        }
        PrintStream ps = new PrintStream(file);
        ps.print(gson.toJson(o));
        ps.close();
    }
}
