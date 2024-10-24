package utils;

import java.awt.*;
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
     * @param dirPath the path to the directory from the root folder
     * @return true if the directory was cleared successfully, false if not
     */

    public static boolean clearDirectory(String dirPath) {
        return clearDirectory(new File(dirPath));
    }

    /**
     * Clears the directory passed in if it exists. If it doesn't exist, creates it.
     * @param directory the file for the directory to clear
     * @return true if the directory was cleared successfully, false if not
     */
    public static boolean clearDirectory(File directory) {
        // If the directory doesn't yet exist, make it
        if (!directory.exists()) {
            return directory.mkdirs();
        }

        boolean success = true;

        // Delete the files that were previously there
        File[] previousFiles = directory.listFiles();

        if (previousFiles == null) {
            return true;
        }

        for (File f : previousFiles) {

            // If a directory has files in it, so it fails to delete,
            // clear it then delete (recursive tree structure for files)
            if (!(f.delete())) {
                if (f.isDirectory()) {
                    success = clearDirectory(f);
                } else {
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
     * @throws IOException if an I/O Exception occurs
     */
    public static String[] readLines(File file)
            throws IOException {
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

    public static boolean writeLines(String[] newLines, File file, boolean append) {
        try (
                FileWriter writer = new FileWriter(file, append)
        ) {
            for (String newLine: newLines) {
                writer.write(newLine);
            }
            return true;
        } catch (FileNotFoundException e) {
            // If the file does not yet exist, make it and then add the line
            boolean success = makeFile(file);

            if (success) {
                return writeLines(newLines, file, append);
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
            file.getParentFile().mkdirs();
            return file.createNewFile();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "File with route %s couldn't be created".formatted(file.getPath()));
        }
        return false;
    }

    public static boolean contains(String text, File file) {
        String[] lines;
        try {
            lines = readLines(file);
        } catch (IOException e) {
            return false;
        }

        for (String s: lines) {
            if (s.contains(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the inputted line from the inputted text file by rewriting the file in full without the chosen line
     * @param text the line to be removed
     * @param file the text file to remove the line from
     * @return true if the removal was successful, else false
     */
    public static boolean removeLine(String text, File file) {
        try {
            String[] currentLines = readLines(file); // read in all the lines
            ArrayList<String> newLines = new ArrayList<>(currentLines.length); // create an ArrayList to store the lines to be rewritten

            for (String s: currentLines) {
                if (!s.equals(text)) { // if the line is not the line to be removed,
                    newLines.add(s + "\n"); // add it, formatted, to the lines to be rewritten
                }
            }

            if (newLines.size() == currentLines.length) { // if
                return false;
            }

            assert file.delete();
            assert file.createNewFile();
            writeLines(newLines.toArray(new String[0]), file, false);

            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return false;
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return ""; // Empty extension
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    public static boolean openFileOnDesktop(File file) {
        if (!Desktop.isDesktopSupported()) {
            return false;
        }

        Desktop desktop = Desktop.getDesktop();

        try {
            if (!file.exists()) {
                return false;
            }

            desktop.open(file);

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
