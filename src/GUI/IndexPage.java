package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IndexPage extends JFrame {

    public IndexPage(int width, int height) {
        super(Constants.indexTitle);

        super.setSize(width, height);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        super.setLayout(new java.awt.FlowLayout());

        JPanel panel = new JPanel();

        panel.add(new JLabel("Hi"));

        super.add(panel);

        super.setVisible(true);
    }

    public void paintImage(BufferedImage image) {
        // FIXME
        JPanel panel = new JPanel();
        panel.setSize(image.getWidth(), image.getHeight());

        Graphics graphics = image.getGraphics();
        panel.paintComponents(graphics);

        graphics.drawImage(image, 0, 0, panel.getWidth(), panel.getHeight(), panel);

        super.getContentPane().add(panel);

        super.pack();
        super.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        IndexPage page = new IndexPage(800, 1000);

        SwingUtilities.invokeLater(() -> {
            try {
                page.paintImage(ImageIO.read(new File("Papers/GCSE/Paper-2013-June-1/pages/page_000.png")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
