import java.awt.image.BufferedImage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFToImage {
    private final static int DPI = 600;

    public static BufferedImage[] toImages(String filePath, int startPage, int endPage) {
        /*
            Converts the PDF to an array of images including the pages starting
            at startPage (inclusive) and ending at endPage (exclusive)
        */
        if (startPage < endPage) {
            try {
                PDDocument document = PDFInterface.getDocument(filePath);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                int numberOfPages = document.getNumberOfPages();
                int end = numberOfPages;
                if ((endPage != -1) || !(endPage <= 0 || endPage > numberOfPages)) {
                    if (endPage != -1) {
                        end = endPage;
                    }

                    BufferedImage[] images = new BufferedImage[end-startPage];

                    for (int i = startPage; i < end; ++i) {
                        images[i] = pdfRenderer.renderImageWithDPI(i, DPI, ImageType.RGB);
                    }
                    document.close();

                    System.out.printf("%d images from PDF (with file path %s) saved", numberOfPages, filePath);

                    return images;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static BufferedImage[] toImages(String filePath) {
        return PDFToImage.toImages(filePath, 0, -1);
    }
}