import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        // ExamPaper paper = new ExamPaper("./papers/GCSE/June-2013/Question-paper/Questionpaper-Paper1-June2017.pdf");
        BufferedImage[] images = PDFToImage.toImages("./papers/GCSE/June-2013/Question-paper/Questionpaper-Paper1-June2017.pdf");
        try {
            ImageIO.write(images[0], "jpg", new File("./image.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}