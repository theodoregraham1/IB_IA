package utils;

import examdocs.ExamPaper;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHandler {
    private static final Logger logger = Logger.getLogger(FileHandler.class.getName());

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

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
    public static String[] readLines(File file) throws IOException {
        ArrayList<String> lines = new ArrayList<>();

        try (
                BufferedReader fileReader = new BufferedReader(new FileReader(file))
        ) {
            String line = fileReader.readLine();

            while (line != null) {
                lines.add(line);
                line = fileReader.readLine();
            }
        }

        return lines.toArray(new String[0]);
    }

    public static boolean addLine(String newLine, File file) {
        try (

                FileWriter writer = new FileWriter(file, true)
        ) {

        } catch (FileNotFoundException e) {
            boolean success = makeFile(file);

            if (success) {
                addLine(newLine, file);
            }
        }
    }

    /**
     *
     * @param file
     * @return
     */
    public static boolean makeFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "File with route %s couldn't be created");
        }
        return false;
    }
}
