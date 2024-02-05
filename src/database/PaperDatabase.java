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

    public final ImageTable pageTable;
    public final ImageTable questionTable;

    private final ExamPaper paper;

    public PaperDatabase(File directory, ExamPaper paper) {
        this.pageTable = new ImageTable(
                new File(directory + PAGES_DIR_NAME),
                TableMode.PAGES
        );

        this.questionTable = new QuestionTable(new File(directory + QUESTIONS_DIR_NAME));

        this.paper = paper;
    }


    private abstract class ImageTable {
        // Optimised mainly for memory usage (not speed)

        protected final TableMode mode;

        protected final File dataFile;
        protected final File imageDir;

        /**
         * Creates a new table of images for the file
         *
         * @param imageDir the directory where the images and the data file are stored
         * @param mode     the type of data for the table to hold
         */
        protected ImageTable(File imageDir, TableMode mode) {
            this.mode = mode;

            if (mode == null) {
                throw new IllegalArgumentException("Mode must not be null");
            }

            this.dataFile = new File(imageDir.getPath() + File.separator + DATABASE_INFO_FILE_NAME);
            this.imageDir = imageDir;

            imageDir.mkdirs();
            FileHandler.makeFile(dataFile);

            if (!imageDir.isDirectory()) {
                throw new IllegalArgumentException("Image directory must be a directory");
            }
        }

        /**
         * Retrieves the selected ImageFiles from the database
         *
         * @param start the first piece of data to be retrieved
         * @param end   the last piece of data to be retrieved (exclusive)
         * @return a list of the requested files
         */
        public ArrayList<ImageFile> getRows(int start, int end) {
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
                            rf.skipBytes(getDataLength() - 1);

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
                            Page[] pages = (Page[]) pageTable.getRows(startPage, endPage).toArray();

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
         *
         * @param image the complete image to be saved
         * @param index the image's index
         * @return the created piece of data (with reference to the created file), null if there was an error
         */
        protected ImageFile saveImage(BufferedImage image, int index) {
            // Make the file to output to, named based on the mode
            File outputFile = ImageFile.getInstanceFile(imageDir, index, mode);

            try {
                ImageIO.write(image,
                        Constants.IMAGE_IO_FORMAT,
                        outputFile);

                logger.log(Level.INFO, "Saved image number %d to file location: %s".formatted(index, outputFile.getCanonicalPath()));

            } catch (IOException e) {
                logger.log(Level.SEVERE, e.toString());
                return null;
            }

            return ImageFile.getInstance(outputFile, mode, logger);
        }

        public abstract boolean addRow(BufferedImage image, int[] data);

        /**
         * Returns the length of each piece of data in the RandomAccessFile
         * @return the number of bytes per ImageFile
         */
        protected abstract int getDataLength();

    }

    private class QuestionTable
            extends ImageTable {

        public QuestionTable(File imageDir) {
            super(imageDir, TableMode.QUESTIONS);
        }

        @Override
        public boolean addRow(BufferedImage image, int[] data) {
            return false;
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

}
