import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;

public class PDFToImage {
    protected final static String IMAGES_DIR_NAME = "/images";
    private final static int DPI = 600;
    public final static String IMAGE_IO_FORMAT = "png";

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
                if ((endPage == -1) || (endPage > 0 && endPage < numberOfPages)) {
                    if (endPage != -1) {
                        end = endPage;
                    }

                    BufferedImage[] images = new BufferedImage[end-startPage];

                    for (int i = startPage; i < end; ++i) {
                        images[i] = pdfRenderer.renderImageWithDPI(i, DPI, ImageType.RGB);
                    }
                    document.close();

                    System.out.printf("%d images from PDF (with file path %s) converted", numberOfPages, filePath);

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

    public static void saveAsImages(String fileName, String dirPath) {
        try {
            // Make the output directory
            File outputDir = new File(dirPath + IMAGES_DIR_NAME);

            if (!(outputDir.exists())) {
                outputDir.mkdirs();
            }
            String outputFileName = "%s/%s".formatted(outputDir.getPath(), fileName.replace(".pdf", ""));

            // Get the document
            PDDocument document = PDFInterface.getDocument(dirPath + fileName);
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int numberOfPages = document.getNumberOfPages();

            // Save all pages as images
            for (int i=0; i<numberOfPages; i++) {
                BufferedImage pageImage = pdfRenderer.renderImageWithDPI(i, DPI, ImageType.RGB);
                ImageIO.write(pageImage,
                        IMAGE_IO_FORMAT,
                        new File("%s_%d.png".formatted(outputFileName, i)));
            }
            document.close();

            System.out.printf("%d images from PDF (with file path %s) saved to %s\n",
                    numberOfPages, dirPath + fileName, outputDir.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}