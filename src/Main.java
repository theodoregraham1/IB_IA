import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        ExamPaper paper = new ExamPaper("Questionpaper-Paper1-June2017.pdf", "./papers/GCSE/June-2013/Question-paper/");
        paper.saveAsImages();

        // Test splitting an image
        File file = new File("./Papers/GCSE/June-2013/Question-paper/images/Questionpaper-Paper1-June2017_0.png");

        try {
            BufferedImage img = ImageIO.read(file);
            int h = img.getHeight();
            int w = img.getWidth();

            BufferedImage halfImage = img.getSubimage(0, h/2, w, h/2);
            System.out.println(ImageIO.write(halfImage, "png", new File("./Papers/output.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}