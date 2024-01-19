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
        paper.makeQuestions();
    }
}