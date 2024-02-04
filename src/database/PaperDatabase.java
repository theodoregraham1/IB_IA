package database;

import examdocs.ExamPaper;
import examdocs.Page;
import examdocs.Question;
import utils.Constants;
import utils.FileHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.Constants.*;


public class PaperDatabase {
    private static final Logger logger = Logger.getLogger(PaperDatabase.class.getName());

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    private final ImageTable pageTable;
    private final ImageTable questionTable;

    private final ExamPaper paper;

    public PaperDatabase(File directory, ExamPaper paper) {
        this.pageTable = new ImageTable(
                new File(directory + PAGES_DIR_NAME),
                TableMode.PAGES
        );

        this.questionTable = new ImageTable(
                new File(directory + QUESTIONS_DIR_NAME),
                TableMode.QUESTIONS
        );

        this.paper = paper;
    }


    private class ImageTable {
        private final TableMode mode;

        private final File dataFile;
        private final File imageDir;

        public ImageTable(File imageDir, TableMode mode) {
            this.mode = mode;

            this.dataFile = new File(imageDir.getPath() + File.separator + DATABASE_INFO_FILE_NAME);
            this.imageDir = imageDir;

            imageDir.mkdirs();
            FileHandler.makeFile(dataFile);

            if (!imageDir.isDirectory()) {
                throw new IllegalArgumentException("Image directory must be a directory");
            }
        }

        public ArrayList<ImageFile> getData(int start, int end) {
            ArrayList<ImageFile> data = new ArrayList<>();

            try (
                    RandomAccessFile rf = new RandomAccessFile(dataFile, "r")
            ) {
                if (end == -1) {
                    end = imageDir.listFiles().length;
                }

                rf.seek((long) start * getDataLength());

                int index = start;

                while (index < end) {
                    index = rf.read();

                    File imageFile = new File(QUESTION_FILE_FORMAT.formatted(imageDir.getPath() + File.separator, index));

                    if (imageFile.exists()) {
                        // If the image exists, get the data from there
                        ImageFile currentData = null;

                        if (mode == TableMode.QUESTIONS) {
                            currentData = new Question(imageFile, logger);

                            // Skip page info
                            rf.skipBytes(getDataLength()-1);

                        } else if (mode == TableMode.PAGES) {
                            currentData = new Page(imageFile, logger);
                        }

                        data.add(currentData);

                    } else {
                        if (mode == TableMode.QUESTIONS) {
                            // Get page information from the file and build the question from that

                            // Get page information
                            int startPage = rf.read();
                            int startPercent = rf.read();

                            int endPage = rf.read();
                            int endPercent = rf.read();

                            // Make the question
                            Page[] pages = (Page[]) pageTable.getData(startPage, endPage).toArray();

                            BufferedImage image = paper.createQuestionImage(pages, startPercent, endPercent);

                            // Save the question
                            data.add(saveImage(image, index));
                        }
                    }

                }
            } catch (IOException e) {
                return data;
            }

            return data;
        }

        /**
         * Save a piece of image data to the directory for this table. Does not add it to the database
         * @param image the complete image to be saved
         * @param index the image's index
         * @return the created piece of data (with reference to the created file), null if there was an error
         */
        private ImageFile saveImage(BufferedImage image, int index) {
            // Make the file to output to, named based on the mode
            File outputFile;

            if (mode == TableMode.QUESTIONS) {
                outputFile = new File(QUESTION_FILE_FORMAT.formatted(imageDir.getPath(), index));

            } else if (mode == TableMode.PAGES) {
                outputFile = new File(PAGE_FILE_FORMAT.formatted(imageDir.getPath(), index));

            } else {
                return null;
            }

            try {
                ImageIO.write(image,
                        Constants.IMAGE_IO_FORMAT,
                        outputFile);

                logger.log(Level.INFO, "Saved image number %d to file location: %s".formatted(index, outputFile.getCanonicalPath()));

            } catch (IOException e) {
                logger.log(Level.SEVERE, e.toString());
                return null;
            }

            if (mode == TableMode.QUESTIONS) {
                return new Question(outputFile, logger);
            } else if (mode == TableMode.PAGES) {
                return new Page(outputFile, logger);
            }
            return null;
        }

        private int getDataLength() {
            if (mode == TableMode.QUESTIONS) {
                return 5;
            }
            // TODO: Add for other modes
            return 0;
        }
    }
}
