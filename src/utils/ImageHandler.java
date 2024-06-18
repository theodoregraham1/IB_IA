package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageHandler {
    public static BufferedImage copyImage(Image original) {
        // Ensure the image is fully loaded
        original = new ImageIcon(original).getImage();

        // Create a buffered image with transparency
        BufferedImage copy = new BufferedImage(original.getWidth(null), original.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on the buffered image
        copy.getGraphics().drawImage(original, 0, 0, null);

        return copy;
    }
}
