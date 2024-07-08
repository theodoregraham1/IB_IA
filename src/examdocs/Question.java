package examdocs;

import database.ImageFile;
import utils.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Question
        implements ImageFile {

    protected Logger logger;
    protected File imageFile;
    protected int marks;

    /**
     * Constructs a new question from its file location
     * @param imageFile the File where the image for this question is stored
     * @param logger the Logger of the owner of this question
     */
    public Question(File imageFile, int marks, Logger logger) {
        this.imageFile = imageFile;
        this.logger = logger;
        this.marks = marks;
    }

    /**
     * Returns a combined image of all the images in a question put together
     * @param inputPages An array of the images for the question, in order
     * @param startPercent The height where the first image is cut off
     * @param endPercent The height where the last image is cut off
     * @return a single Buffered Image made up of all the inputImages joined vertically
     */
    public static BufferedImage createQuestionImage(Page[] inputPages, int startPercent, int endPercent) {

        // Find the sizes for the full pages
        int width = inputPages[0].getImage().getWidth();
        int height = inputPages[0].getImage().getHeight();

        // Find the heights for the partial pages
        int startHeight = height * startPercent / 100;
        int endHeight = height * endPercent / 100;

        // Get cut off images at start and end
        BufferedImage firstImage = inputPages[0].getImage()
                .getSubimage(0, startHeight, width, height-startHeight);
        BufferedImage lastImage = inputPages[inputPages.length-1].getImage()
                .getSubimage(0, 0, width, endHeight);

        // Make the combined image, ready to be filled
        int combinedHeight = height*(inputPages.length-2) + firstImage.getHeight() + lastImage.getHeight();

        BufferedImage combinedImage = new BufferedImage(width, combinedHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = combinedImage.getGraphics();

        // Draw first image
        graphics.drawImage(firstImage, 0, 0, null);

        // Draw middle images
        for (int i = 1; i < inputPages.length-1; i++) {
            graphics.drawImage(
                    inputPages[i].getImage(),
                    0,
                    firstImage.getHeight() + (height * (i-1)),
                    null);
        }

        // Draw last image
        graphics.drawImage(lastImage, 0,combinedHeight - lastImage.getHeight(), null);

        graphics.dispose();

        return combinedImage;
    }

    public int getMarks() {
        return marks;
    }

    @Override
    public File getFile() {
        return this.imageFile;
    }

    /**
     * Gets the image for this question from its file
     * @return this question's image
     */
    @Override
    public BufferedImage getImage() {
        try {
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return null;
    }

    @Override
    public String toString() {
        Path path = imageFile.toPath();
        int nameCount = path.getNameCount();
        String number = path.getName(nameCount-1).toString()
                .replace("question_", "").replace("."+Constants.IMAGE_IO_FORMAT, "")
                .substring(0, 3);
        String paper = path.getName(nameCount-3).toString();
        return paper+" "+number;
    }
}
