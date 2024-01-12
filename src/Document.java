import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.Constants;

import javax.imageio.ImageIO;


public class Document {
    private static final Logger logger = Logger.getLogger(Document.class.getName());

    private final String fileName;
    private final String dirPath;

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    /**
     * Creates new Document instance for a specific pdf file and checks that it is valid
     * @param fileName name of the file not including parent directories
     * @param dirPath route for parent directories
     */
    public Document(String fileName, String dirPath) {
        this.dirPath = dirPath;
        this.fileName = fileName;

        try {
            this.getDocument().close();
        } catch (IOException e) {
            // TODO: More robust error handling
            logger.log(Level.SEVERE, e.toString());

        }
    }

    /**
     * Returns the pdfbox document for this document
     * @return Opened PDDocument for this file
     * @throws IOException if file does not exist or can't be opened
     */
    private PDDocument getDocument() throws IOException {
        // Returns the PDDocument for this file
        String filePath = getFilePath();

        File file = new File(filePath);

        if (!(file.exists())) {
            throw new FileNotFoundException("File at path %s does not exist".formatted(filePath));
        }
        PDDocument pdf = Loader.loadPDF(new File(filePath));

        logger.log(Level.FINER, "PDF loaded with file path: %s".formatted(filePath));

        return pdf;
    }

    /**
     * Returns the text content for this document
     * @return all the text
     */
    public String getText() {
        String text;

        try {
            PDDocument document = this.getDocument();
            PDFTextStripper textStripper = new PDFTextStripper();

            text = textStripper.getText(document);

            document.close();

            logger.log(Level.FINER, "Text stripped from PDF with file path: %s".formatted(getFilePath()));
        } catch (IOException e) {
            text = "";

            logger.log(Level.SEVERE, e.toString());
        }

        return text;
    }

    /**
     * Converts the PDF to an array of images for each page
     * @param startPage the index of the first page to convert (inclusive)
     * @param endPage the index of the last page to convert (exclusive), -1 to convert all the pages
     * @return array of the images
     */
    public BufferedImage[] getImages(int startPage, int endPage) {
        // TODO: Make this take from saved images if they have been saved already

        if (startPage > endPage && endPage != -1) {
            logger.log(Level.SEVERE, "ERROR: Start page must be before end page for Image Conversion");
            return null;
        }

        String filePath = getFilePath();

        try {
            PDDocument document = getDocument();
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int numberOfPages = document.getNumberOfPages();
            int end = numberOfPages;

            if ((endPage == -1) || (endPage > 0 && endPage < numberOfPages)) {
                if (endPage != -1) {
                    end = endPage;
                }

                BufferedImage[] images = new BufferedImage[end-startPage];

                for (int i = startPage; i < end; ++i) {
                    images[i] = pdfRenderer.renderImageWithDPI(i, Constants.DPI, ImageType.RGB);
                }
                document.close();

                logger.log(Level.FINER, "%d images from PDF (with file path %s) converted".formatted(numberOfPages, filePath));

                return images;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }

        return null;
    }

    /**
     * Converts all the pages of the pdf to images
     * @return an array of all the images for the pdf
     */
    public BufferedImage[] getImages() {
        return getImages(0, -1);
    }

    /**
     * Saves all the images for this pdf to files in the same parent directory as the pdf
     * in a directory called '/images/'
     * @return a boolean for whether the access was successful
     */
    public boolean saveAsImages() {
        try {
            // Make the output directory
            File outputDir = new File(dirPath + Constants.IMAGES_DIR_NAME);

            // If the output directory has not been made, make it
            if (!(outputDir.exists())) {
                // Guard clause
                 if (!outputDir.mkdirs()) {
                     return false;
                 }
            }

            // Get name for output files
            String outputFileName = "%s/%s".formatted(
                    outputDir.getPath(),
                    fileName.replace(".pdf", ""));

            // Get the document
            PDDocument document = getDocument();
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int numberOfPages = document.getNumberOfPages();

            // Save all pages as images
            BufferedImage pageImage;
            for (int i=0; i<numberOfPages; i++) {
                pageImage = pdfRenderer.renderImageWithDPI(i, Constants.DPI, ImageType.RGB);

                ImageIO.write(
                        pageImage,
                        Constants.IMAGE_IO_FORMAT,
                        new File("%s_%d.%s".formatted(outputFileName, i, Constants.IMAGE_IO_FORMAT)));
            }
            document.close();

            logger.log(Level.INFO, "%d images from PDF (with file path %s) saved to %s\n".formatted(
                    numberOfPages, dirPath + fileName, outputDir.getPath()));

            return true;

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return false;
    }

    /**
     * Checks whether there are already files in this pdf's images directory
     * @return a boolean value for whether the files have been saved
     */
    public boolean checkImageDir() {
        // Check if this has already been split to images

        boolean saved;

        File imagesDir = new File(dirPath + Constants.IMAGES_DIR_NAME);

        File[] files = imagesDir.listFiles();
        if (files == null)
            saved = false;
        else
            saved = imagesDir.exists()
                    && files.length > 0;

        return saved;
    }

    /**
     * Returns the full filepath to the file from the root directory
     * @return a String of the filepath
     */
    public String getFilePath() {
        return dirPath + fileName;
    }
}
