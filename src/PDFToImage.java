import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;

public class PDFToImage {
    private final static String IMAGES_DIR_NAME = "/images";
    private final static int DPI = 600;
    private final static String IMAGE_IO_FORMAT = "png";
    private final static Logger logger = Logger.getLogger(PDFToImage.class.getName());

    static {
        logger.setLevel(Level.FINEST);
    }

    public static BufferedImage[] toImages(String filePath, int startPage, int endPage) {
        /*
            Converts the PDF to an array of images including the pages starting
            at startPage (inclusive) and ending at endPage (exclusive)
        */

        if (startPage < endPage || endPage == -1) {
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

                    logger.log(Level.FINER, "%d images from PDF (with file path %s) converted".formatted(numberOfPages, filePath));

                    return images;
                }

            } catch (IOException e) {
                logger.log(Level.SEVERE, e.toString());
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
                ImageIO.write(
                        pageImage,
                        IMAGE_IO_FORMAT,
                        new File("%s_%d.png".formatted(outputFileName, i)));
            }
            document.close();

            logger.log(Level.FINER, "%d images from PDF (with file path %s) saved to %s\n".formatted(
                    numberOfPages, dirPath + fileName, outputDir.getPath()));

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString());
        }
    }

    public static boolean checkImageDir(PDFInterface pdfInterface) {
        // Check if pdfInterface has already been split to images

        boolean saved = false;

        File imagesDir = new File(pdfInterface.dirPath + IMAGES_DIR_NAME);

        File[] files = imagesDir.listFiles();
        if (files == null)
            saved = false;
        else
            saved = imagesDir.exists()
                    && Objects.requireNonNull(files.length) > 0;

        return saved;
    }
}