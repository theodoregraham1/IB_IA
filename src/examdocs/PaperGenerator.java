package examdocs;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import utils.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PaperGenerator {
    private final ArrayList<BufferedImage> pages;
    private final int[][] questionsData;
    private int pageNum;
    private int currentHeight;
    private Graphics pageGraphics;

    public PaperGenerator(Question[] questions) {
        this.questionsData = new int[questions.length][4];
        this.pages = new ArrayList<>();

        currentHeight = 0;
        pageNum = -1;
        nextPage();

        // Place each question
        for (int i = 0; i < questions.length; i++) {
            // Get a scaled version of the current question
            BufferedImage currentImage = ImageHandler.copyImage(questions[i].getImage()
                    .getScaledInstance(pages.get(pageNum).getWidth(), -1, Image.SCALE_SMOOTH));

            // Check if the current question will fit on the page
            if (currentHeight + currentImage.getHeight() > pages.get(pageNum).getHeight()) {
                nextPage();
            }

            pageGraphics = pages.get(pageNum).getGraphics();

            int startPage = pageNum;
            int startHeight = currentHeight;

            currentHeight += currentImage.getHeight();

            while (currentImage.getHeight() > pages.get(pageNum).getHeight()) {
                // Draw question if it is bigger than a page, this does not guarantee a good split, but it's good enough
                // and almost always will get a good split

                currentImage = ImageHandler.copyImage(
                        currentImage.getScaledInstance(pages.get(pageNum).getWidth(), -1, Image.SCALE_SMOOTH)
                );

                // Draw first page
                pageGraphics.drawImage(
                        currentImage.getSubimage(0, 0, currentImage.getWidth(), pages.get(pageNum).getHeight()),
                        0,
                        0,
                        null
                );

                nextPage();

                currentImage = currentImage.getSubimage(
                        0,
                        pages.get(pageNum).getHeight(),
                        currentImage.getWidth(),
                        currentImage.getHeight() - pages.get(pageNum).getHeight()
                );
            }

            pageGraphics.drawImage(
                    currentImage,
                    0,
                    startHeight,
                    pages.get(pageNum).getWidth(),
                    currentImage.getHeight(),
                    null
            );

            questionsData[i] = new int[] {
                    startPage,
                    ImageHandler.heightToPercentage(pages.get(pageNum), startHeight),
                    pageNum,
                    ImageHandler.heightToPercentage(pages.get(pageNum), currentHeight)
            };
        }
    }

    private void nextPage() {
        pageNum ++;
        pages.add(pageNum, getNewPageImage());
        currentHeight = 0;
        pageGraphics = pages.get(pageNum).getGraphics();
    }

    private BufferedImage getNewPageImage() {
        return new BufferedImage((int) PDRectangle.A4.getWidth(), (int) PDRectangle.A4.getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    public int[][] getQuestionsData() {
        return questionsData;
    }

    public BufferedImage[] getPages() {
        return pages.toArray(new BufferedImage[0]);
    }
}
