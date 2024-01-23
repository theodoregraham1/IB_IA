package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileHandler {

    /**
     * Clears the directory passed in if it exists. If it doesn't exist, creates it.
     * @param dirPath The path to the directory from the root folder
     * @return true if the directory was cleared successfully, false if not
     */
    public static boolean clearDirectory(String dirPath) {
        File directory = new File(dirPath);

        // Guard clause if the directory doesn't yet exist, it doesn't need to be made
        if (!directory.exists()) {
            return directory.mkdirs();
        }

        boolean success = true;

        // Delete the files that were previously there
        File[] previousFiles = directory.listFiles();

        if (previousFiles != null) {
            for (File f : previousFiles) {

                // If one File fails to delete, fail the whole thing
                if (!(f.delete())) {
                    success = false;
                }
            }
        }
        return success;
    }

    /**
     * Reads all the lines from the specified text file and returns them
     * @param file the file to read from
     * @return an array of all the lines, one line per element
     */
    public static String[] readLines(File file) {
        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line = fileReader.readLine();

            while (line != null) {
                lines.add(line);
                line = fileReader.readLine();
            }
        } catch (IOException e) {

        }
        return lines.toArray(new String[0]);
    }
}
