package net.badbird5907.blib.utils;

import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;

public class FileUtils {
	public static String readFileToString(File file) {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = lines(get(file.getPath()), UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n")); //not the best solution but it works
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

	public static void printObjectToFile(File file, Object o) {
		printObjectToFile(file, o, new Gson());
	}

	@SneakyThrows
	public static void printObjectToFile(File file, Object o, Gson gson) {
		if (!file.exists()) file.createNewFile();
		PrintStream ps = new PrintStream(file);
		ps.print(gson.toJson(o));
		ps.close();
	}
}
