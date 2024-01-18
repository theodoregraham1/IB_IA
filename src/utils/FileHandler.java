package utils;

import java.io.File;

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
                boolean deleted = f.delete();

                // If one File fails to delete, fail the whole thing
                if (!(deleted)) {
                    success = deleted
                }
            }
        }
        return success;
    }
}
