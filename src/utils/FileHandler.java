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

    /**
     * Appends a line to a text file
     * @param newLine the new line (with attached line terminators) to be added
     * @param file the text file to add a line to
     * @return a boolean for whether the line was successfully added
     */
    public static boolean addLine(String newLine, File file) {
        try (
                FileWriter writer = new FileWriter(file, true)
        ) {
            writer.write(newLine);
            return true;
        } catch (FileNotFoundException e) {
            // If the file does not yet exist, make it and then add the line
            boolean success = makeFile(file);

            if (success) {
                return addLine(newLine, file);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return false;
    }

    /**
     * Attempts to create a new file
     * @param file where the new file should be created
     * @return a boolean for whether the file was created
     */
    public static boolean makeFile(File file) {
        try {
            boolean success = file.mkdirs();
            return success = file.createNewFile();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "File with route %s couldn't be created");
        }
        return false;
    }
}
