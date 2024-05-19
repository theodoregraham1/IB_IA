package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageComponent extends JComponent {
    private final BufferedImage image;

    public ImageComponent(BufferedImage image) {
        this.image = image;
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
