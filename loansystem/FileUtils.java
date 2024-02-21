package loansystem;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileUtils {
    public static String getPrettyDateFromFileName(String fileName) {
        String dateTimePart = getDateFromFileName(fileName);

        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimePart, originalFormatter);

        DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        return dateTime.format(newFormatter).toUpperCase();
    }

    public static String getPrettyDateFromDateString(String dateString) {

        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, originalFormatter);

        DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        return dateTime.format(newFormatter).toUpperCase();
    }

    public static String getDateFromFileName(String fileName) {
        return fileName.substring(3, fileName.length() - 4);
    }

    public static String getDateFromFile(File fileName) {
        return fileName.getName().substring(3, fileName.getName().length() - 4);
    }

    public static File createFile() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        File file = new File(getDataFolder(), "TD_" + now.format(formatter) + ".txt");
        if(!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static File getDataFolder() throws IOException {
        File file = new File("TableData");
        if(!file.exists()) {
            file.mkdirs();
            file.createNewFile();
        }
        return file;
    }

    public static File getFile(String fileName) throws IOException {
        if(!fileName.endsWith(".txt")) fileName += ".txt";
        return new File(getDataFolder(), fileName);
    }

    public static List<File> getFiles() throws IOException {
        return Arrays.stream(Objects.requireNonNull(getDataFolder().listFiles())).filter(file -> file.getName().endsWith(".txt")).toList();
    }

    public static List<String> getFileNames() throws IOException {
        return getFiles().stream().map(File::getName).toList();
    }
}
