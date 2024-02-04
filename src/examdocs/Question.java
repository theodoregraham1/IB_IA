package examdocs;

import database.ImageFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Question
        implements ImageFile {

    protected Logger logger;
    protected File imageFile;

    /**
     * Constructs a new question from its file location
     * @param imageFile the File where the image for this question is stored
     * @param logger the Logger of the owner of this question
     */
    public Question(File imageFile, Logger logger) {
        this.imageFile = imageFile;
        this.logger = logger;
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
}
