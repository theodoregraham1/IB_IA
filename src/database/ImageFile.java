package database;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ImageFile {
    /**
     * Get the File associated with this data
     * @return the File holding this data
     */
    File getFile();
    BufferedImage getImage();

}
