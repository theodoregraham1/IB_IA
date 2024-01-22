import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Question {
    File imageFile;
    Logger logger;

    public Question(File imageFile, Logger logger) {
        this.imageFile = imageFile;
        this.logger = logger;
    }

    public BufferedImage getImage() {
        try {
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return null;
    }
}
