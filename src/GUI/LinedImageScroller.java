package GUI;

import utils.ImageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class LinedImageScroller extends ImageScroller {
    private Map<Integer, Color> lines = new HashMap<>();

    public LinedImageScroller(BufferedImage i, int m, int width) {
        super(i, m, width);
    }

    public void addHorizontalLine(int percentage, Color color) {
        lines.put(percentage, color);

        drawLines();
    }

    public void editHorizontalLine(int currentPercentage, int newPercentage) {
        Color color = lines.remove(currentPercentage);
        lines.put(newPercentage, color);
        System.out.println(lines);
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
