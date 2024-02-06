package database;

import examdocs.Page;
import examdocs.Question;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;

import static utils.Constants.PAGE_FILE_FORMAT;
import static utils.Constants.QUESTION_FILE_FORMAT;

public interface ImageFile {
    /**
     * Get the File associated with this data
     * @return the File holding this data
     */
    File getFile();
    BufferedImage getImage();


    static ImageFile getInstance(File file, TableMode mode, Logger logger) {
        if (mode == TableMode.QUESTIONS) {
            return new Question(file, logger);
        } else if (mode == TableMode.PAGES) {
            return new Page(file, logger);
        }
        return null;
    }

    static File getInstanceFile(File parentDirectory, int index, TableMode mode) {

        if (mode == TableMode.QUESTIONS) {
            return new File(QUESTION_FILE_FORMAT.formatted(parentDirectory.getPath(), index));

        } else if (mode == TableMode.PAGES) {
            return new File(PAGE_FILE_FORMAT.formatted(parentDirectory.getPath(), index));

        } else {
            return null;
        }
    }
}
