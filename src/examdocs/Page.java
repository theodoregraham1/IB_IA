package examdocs;

import database.ImageFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Page
    implements ImageFile {

    private final Logger logger;
    private final File imageFile;

    public Page(File file, Logger logger) {
        this.imageFile = file;
        this.logger = logger;
    }

    @Override
    public File getFile() {
        return imageFile;
    }

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
