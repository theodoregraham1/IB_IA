package GUI;

import utils.ImageHandler;
import utils.MultiValueMap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class LinedImageScroller extends ImageScroller {
    private final MultiValueMap<Integer, Color> lines;

    public LinedImageScroller(BufferedImage i, int m, int width) {
        super(i, m, width);

        lines = new MultiValueMap<>();
    }
    public LinedImageScroller(BufferedImage i, int m, int width, MultiValueMap<Integer, Color> lines) {
        super(i, m, width);

        this.lines = lines;
        drawLines();
    }

    public void addHorizontalLine(int percentage, Color color) {
        lines.put(percentage, color);

        drawLines();
    }

    public void editHorizontalLine(int currentPercentage, int newPercentage, Color color) {
        boolean ignored = lines.remove(currentPercentage, color);

        addHorizontalLine(newPercentage, color);
    }

    public void editHorizontalLine(int percentage, Color oldColor, Color newColor) {
        boolean ignored = lines.remove(percentage, oldColor);

        addHorizontalLine(percentage, newColor);
    }

    public void removeHorizontalLine(int percentage, Color color) {
        boolean ignored = lines.remove(percentage, color);

        drawLines();
    }

    private void drawLines() {
        BufferedImage newIcon = ImageHandler.copyImage(masterImage);
        Graphics iconGraphics = newIcon.getGraphics();

        for (Map.Entry<Integer, Color> line: lines.entrySet()) {
            int height = line.getKey() * newIcon.getHeight() / 100;

            iconGraphics.setColor(line.getValue());
            iconGraphics.drawLine(0, height, newIcon.getWidth(), height);
        }

        super.setIcon(new ImageIcon(newIcon));
    }
}
