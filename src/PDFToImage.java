import java.awt.image.BufferedImage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFToImage {
    private final static int DPI = 600;

    public static BufferedImage[] toImages(String filePath) {
        // Converts the PDF to an array of PNGs
        try {
            PDDocument document = PDFInterface.getDocument(filePath);
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int numberOfPages = document.getNumberOfPages();
            BufferedImage[] images = new BufferedImage[numberOfPages];

            for (int i = 0; i < 1; ++i) {
                images[i] = pdfRenderer.renderImageWithDPI(i, DPI, ImageType.RGB);
            }
            document.close();

            System.out.printf("%d images from PDF (with file path %s) saved", numberOfPages, filePath);

            return images;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //public static void saveAsImages()
}