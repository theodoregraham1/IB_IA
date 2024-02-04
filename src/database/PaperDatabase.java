package database;

import examdocs.DocumentPageData;
import examdocs.ExamPaper;
import examdocs.Question;
import utils.FileHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

import static utils.Constants.*;


public class PaperDatabase {
    // TODO: Make design decision of whether this should include page information
    /* Format:
        Question number (1 byte);
        Start page (1 byte);
        Percent start height (1 byte);
        End page (1 byte);
        Percent end height (1 byte);
     */

    private final Logger logger;

    private final ImageTable pageTable;
    private final ImageTable questionTable;

    private final ExamPaper paper;

    public PaperDatabase(File directory, ExamPaper paper, Logger logger) {
        this.pageTable = new ImageTable(
                new File(directory + PAGES_DIR_NAME),
                TableMode.PAGES,
                logger
        );

        this.questionTable = new ImageTable(
                new File(directory + QUESTIONS_DIR_NAME),
                TableMode.QUESTIONS,
                logger
        );

        this.paper = paper;
        this.logger = logger;
    }



    private class ImageTable {
        private final TableMode mode;
        private final Logger logger;

        private final File dataFile;
        private final File imageDir;

        public ImageTable(File imageDir, TableMode mode, Logger logger) {
            this.dataFile = new File(imageDir.getPath() + File.separator + DATABASE_INFO_FILE_NAME);
            this.imageDir = imageDir;

            if (!imageDir.isDirectory()) {
                throw new IllegalArgumentException("Image directory must be a directory");
            }

            FileHandler.makeFile(dataFile);
            FileHandler.makeFile(imageDir);

            this.mode = mode;
            this.logger = logger;
        }

        public DocumentPageData[] getQuestions() {
            ArrayList<DocumentPageData> images = new ArrayList<>();

            try (
                    RandomAccessFile rf = new RandomAccessFile(dataFile, "r")
            ) {
                rf.seek(0);
                while (true) {
                    int index = rf.read();

                    File imageFile = new File(QUESTION_FILE_FORMAT.formatted(imageDir.getPath() + File.separator, index));

                    if (imageFile.exists()) {
                        // If the image exists, get the data from there
                        DocumentPageData data = null;

                        if (mode == TableMode.QUESTIONS) {
                            images.add(index, new Question(imageFile, logger));
                        }
                        // Skip page info
                        rf.skipBytes(4);
                    } else {
                        int startPage = rf.read();
                        int startPercent = rf.read();

                        int endPage = rf.read();
                        int endPercent = rf.read();

                        paper.
                    }

                }
            } catch (IOException e) {

            }

            return images.toArray(new DocumentPageData[0]);
        }
    };
}
