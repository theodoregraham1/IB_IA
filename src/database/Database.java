package database;

import utils.Constants;
import utils.FileHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.Constants.DATABASE_INFO_FILE_NAME;

public class Database {
    protected static final Logger logger = Logger.getLogger(PaperDatabase.class.getName());

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }


    public abstract class ImageTable<T> {
        // Optimised mainly for memory usage (not speed)

        protected final File dataFile;
        protected final File imageDir;

        /**
         * Creates a new table of images for the file
         * @param imageDir the directory where the images and the data file are stored
         */
        protected ImageTable(File imageDir) {
            if (!imageDir.isDirectory()) {
                if (!imageDir.mkdirs()) throw new IllegalArgumentException("Image directory must be a directory");
            }

            this.dataFile = new File(imageDir, DATABASE_INFO_FILE_NAME);
            this.imageDir = imageDir;

            FileHandler.makeFile(dataFile);
        }

        /**
         * Retrieves the selected ImageFiles from the database
         * @param start the first piece of data to be retrieved
         * @param end   the last piece of data to be retrieved (exclusive)
         * @return a list of the requested files
         */
        public ArrayList<T> getRows(int start, int end) {
            ArrayList<T> data = new ArrayList<>();

            try (
                    RandomAccessFile rf = new RandomAccessFile(dataFile, "r")
            ) {
                if (end == -1) {
                    try {
                        end = Objects.requireNonNull(imageDir.listFiles()).length;
                    } catch (NullPointerException e) {
                        end = 0;
                    }
                }

                rf.seek((long) start * getDataLength());

                int index = rf.read();
                while (index < end && index != -1) {

                    File imageFile = getInstanceFile(index, -1);

                    if (Objects.requireNonNull(imageFile).exists()) {
                        // If the image exists, get the data from there
                        T currentData = getObjectInstance(imageFile);
                        rf.skipBytes(getDataLength() - 1);

                        data.add(currentData);
                    } else {
                        data.add(generateObjectInstance(rf, index));
                    }
                    index = rf.read();
                }
            } catch (IOException | NullPointerException e) {
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
        protected T saveImage(BufferedImage image, int index, int extraData) {
            // Make the file to output to, named based on the mode
            File outputFile = getInstanceFile(index, extraData);

            try {
                ImageIO.write(
                        image,
                        Constants.IMAGE_IO_FORMAT,
                        Objects.requireNonNull(outputFile)
                );

                logger.log(Level.INFO, "Saved image number %d to file location: %s".formatted(index, outputFile.getCanonicalPath()));

            } catch (IOException | NullPointerException e) {
                logger.log(Level.SEVERE, e.toString());
                return null;
            }
            return getObjectInstance(outputFile);
        }

        public void setRow(BufferedImage image, int[] data) {
            if (data.length != getDataLength()) {
                return;
            }

            saveImage(image, data[0], data[data.length-1]);

            // Convert data to bytes for writing
            byte[] bytes = new byte[data.length];

            for (int i = 0; i < data.length; i++) {
                bytes[i] = (byte) data[i];
            }

            try (
                    RandomAccessFile rf = new RandomAccessFile(dataFile, "rw")
            ) {
                rf.seek((long) data[0] * getDataLength());

                rf.write(bytes);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.toString());
            }

        }

        public int length() {
            try (
                    RandomAccessFile rf = new RandomAccessFile(dataFile, "r")
            ) {
                return (int) rf.length() / getDataLength();
            } catch (IOException e) {
                return 0;
            }
        }

        /**
         * Returns the length of each piece of data in the RandomAccessFile
         *
         * @return the number of bytes per ImageFile
         */
        protected abstract int getDataLength();

        protected abstract T getObjectInstance(File file);

        protected abstract File getInstanceFile(int index, int extraData);

        protected abstract T generateObjectInstance(RandomAccessFile rf, int index) throws IOException;
    }
}
