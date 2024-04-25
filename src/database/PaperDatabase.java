package database;

import examdocs.Document;
import examdocs.Page;
import examdocs.Question;
import utils.Constants;
import utils.FileHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.Constants.*;


public class PaperDatabase extends Database {
    public final PageTable pageTable;
    public final QuestionTable questionTable;

    public PaperDatabase(File directory, Document document) {
        this.pageTable = new PageTable(new File(directory, PAGES_DIR_NAME), document);
        this.questionTable = new QuestionTable(new File(directory, QUESTIONS_DIR_NAME));
    }

    public class QuestionTable
            extends ImageTable {

        public QuestionTable(File imageDir) {
            super(imageDir, TableMode.QUESTIONS);
        }

        public void setRow(int[] data) {
            if (data.length != getDataLength()) {
                return;
            }
            super.setRow(
                    Question.createQuestionImage(
                            pageTable.getRows(data[1], data[3]).toArray(new Page[0]),
                            data[2],
                            data[4]
                    ),
                    data
            );
        }

        /**
         * Returns the length of each piece of data in the RandomAccessFile
         * @return the number of bytes per ImageFile
         */
        @Override
        protected int getDataLength() {
            return 5;
        }
    }

    public class PageTable
        extends ImageTable {

        private Document document;

        public PageTable(File imageDir, Document document) {
            super(imageDir, TableMode.PAGES);

            this.document = document;
            makeFromDocument();
        }

        public void makeFromDocument() {
            // Assume document is not too large where images will overflow memory

            try (RandomAccessFile rf = new RandomAccessFile(dataFile, "r")) {
                // Check if the document has already been saved
                if (((long) document.length() * getDataLength()) <= rf.length()
                        && dataFile.getParentFile().list().length-1 == document.length()) {
                    return;
                }
                rf.close();

                // Destroy and recreate the data file
                if (!dataFile.delete()) {
                    return;
                }
                if (!dataFile.createNewFile()) {
                    return;
                }
            } catch (FileNotFoundException e) {
                FileHandler.clearDirectory(dataFile.getParent());
                FileHandler.makeFile(dataFile);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Write the images
            int index = 0;
            for (BufferedImage image: document.splitToImages()) {
                setRow(image, new int[]{index});
                index ++;
            }
        }

        /**
         * Returns the length of each piece of data in the RandomAccessFile
         * @return the number of bytes per ImageFile
         */
        @Override
        protected int getDataLength() {
            return 1;
        }
    }

    public static void makeDatabase(File directory, File newPaper) {
        // Save the paper
        File paperFile = new File(directory, PAPER_FILE_NAME);

        boolean replace = false;
        if (paperFile.exists()) {
            if (!newPaper.equals(paperFile)) {
                // If the current file is not the same as the new one (based on abstract paths), replace it
                replace = true;
            }
        } else {
            replace = true;
        }

        if (replace) {
            try {
                Files.copy(newPaper.toPath(), paperFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
