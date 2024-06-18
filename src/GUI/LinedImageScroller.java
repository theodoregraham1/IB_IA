package GUI;

import utils.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class LinedImageScroller extends ImageScroller {
    Map<Integer, Color> lines = new HashMap<>();

    public LinedImageScroller(BufferedImage i, int m, int width) {
        super(i, m, width);
    }

    public void addHorizontalLine(int percentage, Color color) {

        BufferedImage image = ImageHandler.copyImage(masterImage);

    }
}
