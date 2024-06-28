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
        if (lines.containsKey(percentage)) {
            lines.put(percentage+1, color);
        } else {
            lines.put(percentage, color);
        }

        drawLines();
    }

    public void editHorizontalLine(int currentPercentage, int newPercentage, Color color) {
        Color oldColor = lines.remove(currentPercentage);

        if (!oldColor.equals(color)) {
            lines.put(currentPercentage, oldColor);
            return;
        }

        if (lines.containsKey(newPercentage)) {
            lines.put(newPercentage+1, color);
        } else {
            lines.put(newPercentage, color);
        }

        drawLines();
    }

    public void editHorizontalLine(int percentage, Color oldColor, Color newColor) {
        Color currentColor = lines.remove(percentage);

        if (oldColor.equals(currentColor)) {
            lines.put(percentage, newColor);
        } else {
            lines.put(percentage, currentColor);
        }

        drawLines();
    }

    public void removeHorizontalLine(int percentage, Color color) {
        Color oldColor = lines.remove(percentage);
        if (!oldColor.equals(color)) {
            lines.put(percentage, color);
        }
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
