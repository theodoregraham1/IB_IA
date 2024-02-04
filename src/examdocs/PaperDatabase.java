package examdocs;

import utils.FileHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

import static utils.Constants.QUESTION_FILE_FORMAT;


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
    private final File dataFile;
    private final File imageDir;

    public PaperDatabase(File dataFile, File imageDir, Logger logger) {
        this.dataFile = dataFile;
        this.imageDir = imageDir;

        FileHandler.makeFile(dataFile);
        FileHandler.makeFile(imageDir);

        this.logger = logger;
    }

    public Question[] getQuestions() {
        ArrayList<Question> questions = new ArrayList<>();

        try (
                RandomAccessFile rf = new RandomAccessFile(dataFile, "r")
        ) {
            rf.seek(0);
            while (true) {
                int index = rf.read();



                File imageFile = new File(QUESTION_FILE_FORMAT.formatted(imageDir.getPath() + File.separator, index));

                if (imageFile.exists()) {
                    // If the image exists, get the data from there
                    questions.add(index, new Question(imageFile, logger));

                    // Skip page info
                    rf.skipBytes(4);
                }

            }
        } catch (IOException e) {
            ;
        }
    }

    private class ImageTable {
        private final File dataFile;
        private final File imageDir;

        public Table(File dataFile, File imageDir, Mode mode) {
            this.dataFile = dataFile;
            this.imageDir = imageDir;

            FileHandler.makeFile(dataFile);
            FileHandler.makeFile(imageDir);
        }
    }

    public enum Mode {
        QUESTIONS,
        PAGES,
    };
}
