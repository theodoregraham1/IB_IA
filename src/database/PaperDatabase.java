package database;

import examdocs.Document;
import examdocs.ExamPaper;
import examdocs.Page;
import examdocs.Question;
import org.bouncycastle.asn1.cmp.Challenge;
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

    public final PageTable pageTable;
    public final QuestionTable questionTable;

    public PaperDatabase(File directory) {
        this.pageTable = new PageTable(new File(directory + PAGES_DIR_NAME));
        this.questionTable = new QuestionTable(new File(directory + QUESTIONS_DIR_NAME));
    }

    public abstract class ImageTable {
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

            this.dataFile = new File(imageDir, DATABASE_INFO_FILE_NAME);
            this.imageDir = imageDir;

            imageDir.mkdirs();
            FileHandler.makeFile(imageDir);
            FileHandler.makeFile(dataFile);

            if (!imageDir.isDirectory()) {
                throw new IllegalArgumentException("Image directory must be a directory");
            }
        }

        /**
         * Retrieves the selected ImageFiles from the database
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

                int index = rf.read();

                while (index < end && index != -1) {

                    File imageFile = new File(QUESTION_FILE_FORMAT.formatted(imageDir.getPath() + File.separator, index));

                    if (imageFile.exists()) {
                        // If the image exists, get the data from there
                        ImageFile currentData = ImageFile.getInstance(imageFile, mode, logger);
                        rf.skipBytes(getDataLength()-1);

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
                            Page[] pages = pageTable.getRows(startPage, endPage).toArray(new Page[0]);

                            BufferedImage image = Question.createQuestionImage(pages, startPercent, endPercent);

                            // Save the question
                            data.add(saveImage(image, index));
                        }
                    }
                    index = rf.read();
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
            assert outputFile != null;

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

        public boolean setRow(BufferedImage image, int[] data) {
            if (data.length != getDataLength()) {
                return false;
            }

            // Convert data to bytes for writing
            byte[] bytes = new byte[data.length];

            for (int i = 0; i < data.length; i++) {
                bytes[i] = (byte) data[i];
            }

            try (
                    RandomAccessFile rf = new RandomAccessFile(dataFile, "rw");
            ) {
                rf.seek((long) data[0] * getDataLength());

                rf.write(bytes);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.toString());
                return false;
            }
            saveImage(image, data[0]);

            return true;
        }

        /**
         * Returns the length of each piece of data in the RandomAccessFile
         * @return the number of bytes per ImageFile
         */
        protected abstract int getDataLength();

    }

    public class QuestionTable
            extends ImageTable {

        public QuestionTable(File imageDir) {
            super(imageDir, TableMode.QUESTIONS);
        }

        public boolean setRow(int[] data) {
            if (data.length != getDataLength()) {
                return false;
            }
            return super.setRow(
                    Question.createQuestionImage(
                        (Page[]) pageTable.getRows(data[1], data[3] + 1).toArray(new ImageFile[0]),
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

        public PageTable(File imageDir) {
            super(imageDir, TableMode.PAGES);
        }

        public boolean makeFromDocument(Document document) {
            // Assume document is not too large where images will overflow memory

            try (RandomAccessFile rf = new RandomAccessFile(dataFile, "r")) {
                // Check if the document has already been saved
                if (((long) document.length() * getDataLength()) == rf.length()) {
                    return true;
                }
                rf.close();

                // Destroy and recreate the data file
                if (!dataFile.delete()) {
                    return false;
                }
                if (!dataFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Write the images
            int index = 0;
            for (BufferedImage image: document.splitToImages()) {
                setRow(image, new int[]{index}); // FIXME: This tries to access the same file twice at the same time
                index ++;
            }
            return true;
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

}
